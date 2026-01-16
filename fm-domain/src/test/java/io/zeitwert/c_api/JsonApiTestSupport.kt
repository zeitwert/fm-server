package io.zeitwert.c_api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions
import org.springframework.http.MediaType
import java.math.BigDecimal

object JsonApiTestSupport {

	val JSON_API_CONTENT_TYPE: MediaType = MediaType.parseMediaType("application/vnd.api+json")

	@Suppress("UNCHECKED_CAST")
	fun parseJsonApiResponse(
		objectMapper: ObjectMapper,
		json: String,
	): Map<String, Any?> {
		val response: Map<String, Any?> = objectMapper.readValue(json)
		val data = response["data"] as Map<String, Any?>
		return mapOf(
			"id" to data["id"],
			"type" to data["type"],
			"attributes" to data["attributes"],
			"meta" to data["meta"],
			"relationships" to data["relationships"],
			"included" to response["included"],
		)
	}

	@Suppress("UNCHECKED_CAST")
	fun requireAttributes(response: Map<String, Any?>): Map<String, Any?> {
		val attributes = response["attributes"] as? Map<String, Any?>
		Assertions.assertNotNull(attributes, "Response should have attributes")
		return attributes!!
	}

	@Suppress("UNCHECKED_CAST")
	fun requireMeta(response: Map<String, Any?>): Map<String, Any?> {
		val meta = response["meta"] as? Map<String, Any?>
		Assertions.assertNotNull(meta, "Response should have meta")
		return meta!!
	}

	@Suppress("UNCHECKED_CAST")
	fun requireRelationships(response: Map<String, Any?>): Map<String, Any?> {
		val relationships = response["relationships"] as? Map<String, Any?>
		Assertions.assertNotNull(relationships, "Response should have relationships")
		return relationships!!
	}

	@Suppress("UNCHECKED_CAST")
	fun requireRelationshipData(
		relationships: Map<String, Any?>,
		relationName: String,
	): Map<String, Any?> {
		val relation = relationships[relationName] as? Map<String, Any?>
		Assertions.assertNotNull(relation, "Relationships should have $relationName")
		val data = relation!!["data"] as? Map<String, Any?>
		Assertions.assertNotNull(data, "$relationName should have data")
		return data!!
	}

	@Suppress("UNCHECKED_CAST")
	fun requireRelationshipDataList(
		relationships: Map<String, Any?>,
		relationName: String,
	): List<Map<String, Any?>> {
		val relation = relationships[relationName] as? Map<String, Any?>
		Assertions.assertNotNull(relation, "Relationships should have $relationName")
		val data = relation!!["data"] as? List<Map<String, Any?>>
		Assertions.assertNotNull(data, "$relationName should have data list")
		return data!!
	}

	fun verifyRelationshipData(
		data: Map<String, Any?>,
		expectedType: String,
		expectedId: String,
	) {
		Assertions.assertEquals(expectedType, data["type"], "Relationship type should match")
		Assertions.assertEquals(expectedId, data["id"], "Relationship id should match")
	}

	fun verifyRelationshipListContains(
		dataList: List<Map<String, Any?>>,
		expectedType: String,
		expectedId: String,
	) {
		val match = dataList.any { it["type"] == expectedType && it["id"] == expectedId }
		Assertions.assertTrue(
			match,
			"Expected relationship list to contain $expectedType/$expectedId",
		)
	}

	fun extractVersion(meta: Map<String, Any?>): Int {
		val version = meta["version"]
		Assertions.assertNotNull(version, "Meta should have version")
		return when (version) {
			is Int -> version
			is Number -> version.toInt()
			is String -> version.toInt()
			else -> throw AssertionError("Unexpected version type: ${version?.javaClass}")
		}
	}

	fun verifyEnumField(
		actual: Any?,
		expectedId: String,
	) {
		Assertions.assertNotNull(actual, "Enum field should not be null")
		when (actual) {
			is Map<*, *> -> Assertions.assertEquals(expectedId, actual["id"], "Enum id should match")
			is String -> Assertions.assertEquals(expectedId, actual, "Enum id should match")
			else -> throw AssertionError("Unexpected enum field type: ${actual?.javaClass}")
		}
	}

	fun verifyBigDecimal(
		actual: Any?,
		expected: BigDecimal,
	) {
		Assertions.assertNotNull(actual, "BigDecimal field should not be null")
		val actualBigDecimal =
			when (actual) {
				is BigDecimal -> actual
				is Number -> BigDecimal.valueOf(actual.toDouble())
				is String -> BigDecimal(actual)
				else -> throw AssertionError("Unexpected BigDecimal field type: ${actual?.javaClass}")
			}
		Assertions.assertEquals(0, expected.compareTo(actualBigDecimal), "BigDecimal values should match")
	}

	fun verifyEnumeratedListContains(
		actual: Any?,
		expectedId: String,
	) {
		Assertions.assertNotNull(actual, "Enumerated list should not be null")
		val list = actual as? List<*> ?: throw AssertionError("Enumerated list should be a List")
		val ids = list.map { item ->
			when (item) {
				is Map<*, *> -> item["id"] as String?
				is String -> item
				else -> throw AssertionError("Unexpected enumerated list item type: ${item?.javaClass}")
			}
		}
		Assertions.assertTrue(ids.contains(expectedId), "Expected list to contain id $expectedId")
	}

	fun verifyClosedMeta(meta: Map<String, Any?>) {
		Assertions.assertNotNull(meta["closedAt"], "closedAt should be set after delete")
		Assertions.assertNotNull(meta["closedByUser"], "closedByUser should be set after delete")
	}

	/**
	 * Verify tenant info is present in the meta section.
	 *
	 * The tenant info is always included in meta for all aggregates,
	 * providing id, name, and itemType.
	 */
	@Suppress("UNCHECKED_CAST")
	fun verifyTenantInMeta(
		meta: Map<*, *>,
		expectedTenantId: String,
		expectedTenantName: String,
		expectedItemTypeId: String = "obj_tenant",
	) {
		val tenantMeta = meta["tenant"] as? Map<String, Any?>
		Assertions.assertNotNull(tenantMeta, "Meta should have tenant info")

		Assertions.assertEquals(expectedTenantId, tenantMeta!!["id"], "Tenant id should match")
		Assertions.assertEquals(expectedTenantName, tenantMeta["name"], "Tenant name should match")

		// Verify itemType is present
		val itemType = tenantMeta["itemType"] as? Map<String, Any?>
		Assertions.assertNotNull(itemType, "Tenant should have itemType")
		Assertions.assertEquals(expectedItemTypeId, itemType!!["id"], "Tenant itemType id should match")
	}

	/**
	 * Verify tenantInfo relationship and included tenant aggregate.
	 *
	 * When ?include=tenantInfo is used, the response should contain:
	 * - A relationships.tenantInfo section with a link to the tenant
	 * - An included array with the full tenant resource
	 */
	@Suppress("UNCHECKED_CAST")
	fun verifyTenantRelationAndInclude(
		response: Map<String, Any?>,
		expectedTenantId: String,
		expectedTenantKey: String = "test",
		expectedTenantName: String = "Test",
		expectedTenantTypeId: String = "advisor",
	) {
		// Verify relationships section exists
		val relationships = response["relationships"] as? Map<String, Any?>
		Assertions.assertNotNull(relationships, "Response should have relationships")

		// Verify tenantInfo relationship
		val tenantInfoRel = relationships!!["tenantInfo"] as? Map<String, Any?>
		Assertions.assertNotNull(tenantInfoRel, "Relationships should have tenantInfo")

		val tenantInfoData = tenantInfoRel!!["data"] as? Map<String, Any?>
		Assertions.assertNotNull(tenantInfoData, "tenantInfo should have data")
		Assertions.assertEquals("tenant", tenantInfoData!!["type"], "tenantInfo type should be 'tenant'")
		Assertions.assertEquals(expectedTenantId, tenantInfoData["id"], "tenantInfo id should match")

		// Verify included array contains the tenant
		val included = response["included"] as? List<Map<String, Any?>>
		Assertions.assertNotNull(included, "Response should have included array")
		Assertions.assertTrue(included!!.isNotEmpty(), "Included array should not be empty")

		// Find the tenant in included array
		val includedTenant = included.find { it["type"] == "tenant" && it["id"] == expectedTenantId }
		Assertions.assertNotNull(includedTenant, "Included array should contain tenant")

		// Verify tenant attributes
		val tenantAttributes = includedTenant!!["attributes"] as? Map<String, Any?>
		Assertions.assertNotNull(tenantAttributes, "Included tenant should have attributes")
		Assertions.assertEquals(expectedTenantKey, tenantAttributes!!["key"], "Tenant key should match")
		Assertions.assertEquals(expectedTenantName, tenantAttributes["name"], "Tenant name should match")
		verifyEnumField(tenantAttributes["tenantType"], expectedTenantTypeId)
	}
}
