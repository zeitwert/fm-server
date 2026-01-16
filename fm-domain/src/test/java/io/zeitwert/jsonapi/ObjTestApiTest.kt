package io.zeitwert.jsonapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

/**
 * Integration test for ObjTest CRUD operations via JSON:API endpoints using MockMVC.
 *
 * Tests the full lifecycle: Create -> Read -> Update -> Read -> Delete -> Read (verify closed)
 *
 * Uses the test tenant and test user configured in TestSessionContextProvider:
 * - User: tt@zeitwert.io
 * - Account: TA
 *
 * Note: @DirtiesContext is used to ensure a fresh Spring context since persistence providers
 * register with a singleton RepositoryDirectory that enforces uniqueness.
 */
@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithMockUser(username = "tt@zeitwert.io", roles = ["USER"])
class ObjTestApiTest {

	companion object {

		@JvmStatic
		@BeforeAll
		fun resetRepositoryDirectory() {
			// Reset providers/repositories but preserve enumerations for the new test context
			RepositoryDirectory.resetProvidersAndRepositories()
		}

		private const val API_PATH = "/api/test/objTests"
		private val JSON_API_CONTENT_TYPE = MediaType.parseMediaType("application/vnd.api+json")

		private const val TEST_SHORT_TEXT = "Test Short Text"
		private const val TEST_LONG_TEXT = "Test Long Text"
		private const val TEST_INT = 42
		private const val TEST_NR = 42.5
		private const val TEST_DATE = "1966-09-08"
		private const val TEST_IS_DONE = false
		private const val TEST_TYPE_ID = "type_a"

		private const val UPDATED_SHORT_TEXT = "Updated Short Text"
		private const val UPDATED_LONG_TEXT = "Updated Long Text"
		private const val UPDATED_INT = 99
		private const val UPDATED_NR = 99.9
		private const val UPDATED_DATE = "2025-01-14"
		private const val UPDATED_IS_DONE = true
		private const val UPDATED_TYPE_ID = "type_b"
	}

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Test
	fun `test Create and Read for ObjTest via JSON API`() {
		// === CREATE ===
		val createPayload = createObjTestPayload()
		val createResult = mockMvc
			.perform(
				post(API_PATH)
					.with(csrf().asHeader())
					.contentType(JSON_API_CONTENT_TYPE)
					.accept(JSON_API_CONTENT_TYPE)
					.content(createPayload),
			).andExpect(status().isCreated)
			.andReturn()

		val createResponse = parseJsonApiResponse(createResult.response.contentAsString)
		val createdId = createResponse["id"] as String
		assertNotNull(createdId, "Created object should have an ID")

		// Verify created attributes using shared verification method
		val createAttributes = createResponse["attributes"] as Map<*, *>
		verifyInitialAttributes(createAttributes)

		// Verify nodeList was created with 2 nodes, capture their IDs
		val createNodeList = createAttributes["nodeList"] as List<*>
		val (nodeAId, nodeBId) = verifyInitialNodeList(createNodeList)

		// Verify meta fields
		val createMeta = createResponse["meta"] as Map<*, *>
		assertNotNull(createMeta["version"], "Meta should have version")
		assertEquals(1, createMeta["version"])
		assertNotNull(createMeta["createdAt"], "Meta should have createdAt")
		assertNotNull(createMeta["createdByUser"], "Meta should have createdByUser")
		assertNull(createMeta["closedAt"], "closedAt should be null for new object")
		assertNull(createMeta["closedByUser"], "closedByUser should be null for new object")

		// === READ (with tenantInfo include) ===
		val readResult = mockMvc
			.perform(
				get("$API_PATH/$createdId?include=tenantInfo").accept(JSON_API_CONTENT_TYPE),
			).andExpect(status().isOk)
			.andReturn()

		val readResponse = parseJsonApiResponse(readResult.response.contentAsString)
		assertEquals(createdId, readResponse["id"])

		// Verify read attributes using shared verification method
		val readAttributes = readResponse["attributes"] as Map<*, *>
		verifyInitialAttributes(readAttributes)

		// Verify nodeList using shared verification method
		val readNodeList = readAttributes["nodeList"] as List<*>
		verifyInitialNodeList(readNodeList)

		val readMeta = readResponse["meta"] as Map<*, *>
		assertNull(readMeta["closedAt"], "closedAt should still be null")
		assertNull(readMeta["closedByUser"], "closedByUser should still be null")

		// Verify tenant info is in meta (always present)
		verifyTenantInMeta(readMeta)

		// Verify tenantInfo relationship and included tenant aggregate
		verifyTenantRelationAndInclude(readResponse)

		// === UPDATE ===
		// Update will: UPDATE Node A (by ID), INSERT Node C (no ID), DELETE Node B (omitted)
		val version = readMeta["version"] as Int
		val updatePayload = createUpdatePayload(createdId, version, nodeAId)
		val updateResult = mockMvc
			.perform(
				patch("$API_PATH/$createdId")
					.with(csrf().asHeader())
					.contentType(JSON_API_CONTENT_TYPE)
					.accept(JSON_API_CONTENT_TYPE)
					.content(updatePayload),
			).andExpect(status().isOk)
			.andReturn()

		run {
			val updateResponse = parseJsonApiResponse(updateResult.response.contentAsString)
			assertEquals(createdId, updateResponse["id"])

			// Verify updated attributes using shared verification method
			val updatedAttributes = updateResponse["attributes"] as Map<*, *>
			verifyUpdatedAttributes(updatedAttributes)

			// Verify nodeList: Node A updated, Node B deleted, Node C inserted
			val updatedNodeList = updatedAttributes["nodeList"] as List<*>
			verifyUpdatedNodeList(updatedNodeList, nodeAId, nodeBId)

			// Verify meta - version should be incremented
			val updatedMeta = updateResponse["meta"] as Map<*, *>
			val newVersion = updatedMeta["version"] as Int
			assertTrue(newVersion > version, "Version should be incremented after update")
			assertNotNull(updatedMeta["modifiedAt"], "modifiedAt should be set after update")
			assertNotNull(updatedMeta["modifiedByUser"], "modifiedByUser should be set after update")
			assertNull(updatedMeta["closedAt"], "closedAt should still be null")
			assertNull(updatedMeta["closedByUser"], "closedByUser should still be null")
		}

		// === READ AFTER UPDATE ===
		val readUpdatedResult = mockMvc
			.perform(
				get("$API_PATH/$createdId").accept(JSON_API_CONTENT_TYPE),
			).andExpect(status().isOk)
			.andReturn()

		val readUpdatedResponse = parseJsonApiResponse(readUpdatedResult.response.contentAsString)

		// Verify updated attributes using shared verification method
		val updatedAttributes = readUpdatedResponse["attributes"] as Map<*, *>
		verifyUpdatedAttributes(updatedAttributes)

		// Verify nodeList: Node A updated, Node B deleted, Node C inserted
		val updatedNodeList = updatedAttributes["nodeList"] as List<*>
		verifyUpdatedNodeList(updatedNodeList, nodeAId, nodeBId)

		// Verify meta - version should be incremented
		val updatedMeta = readUpdatedResponse["meta"] as Map<*, *>
		val newVersion = updatedMeta["version"] as Int
		assertTrue(newVersion > version, "Version should be incremented after update")
		assertNotNull(updatedMeta["modifiedAt"], "modifiedAt should be set after update")
		assertNotNull(updatedMeta["modifiedByUser"], "modifiedByUser should be set after update")
		assertNull(updatedMeta["closedAt"], "closedAt should still be null")
		assertNull(updatedMeta["closedByUser"], "closedByUser should still be null")

		// === DELETE (soft delete / close) ===
		mockMvc
			.perform(
				delete("$API_PATH/$createdId")
					.with(csrf().asHeader())
					.accept(JSON_API_CONTENT_TYPE),
			).andExpect(status().isNoContent)

		// === READ AFTER DELETE (verify closed) ===
		val readClosedResult = mockMvc
			.perform(
				get("$API_PATH/$createdId").accept(JSON_API_CONTENT_TYPE),
			).andExpect(status().isOk)
			.andReturn()

		val readClosedResponse = parseJsonApiResponse(readClosedResult.response.contentAsString)
		val closedMeta = readClosedResponse["meta"] as Map<*, *>

		assertNotNull(closedMeta["closedAt"], "closedAt should be set after delete")
		assertNotNull(closedMeta["closedByUser"], "closedByUser should be set after delete")
	}

	private fun createObjTestPayload(): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "objTest",
				"attributes" to mapOf(
					"shortText" to TEST_SHORT_TEXT,
					"longText" to TEST_LONG_TEXT,
					"integer" to TEST_INT,
					"nr" to TEST_NR,
					"date" to TEST_DATE,
					"isDone" to TEST_IS_DONE,
					"testType" to mapOf("id" to TEST_TYPE_ID, "name" to "Type A"),
					"nodeList" to listOf(
						mapOf(
							"shortText" to "Node A",
							"integer" to 1,
						),
						mapOf(
							"shortText" to "Node B",
							"integer" to 2,
						),
					),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	/**
	 * Create update payload that:
	 * - Updates Node A (by providing its ID)
	 * - Inserts Node C (no ID provided)
	 * - Deletes Node B (omitted from payload)
	 */
	private fun createUpdatePayload(
		id: String,
		clientVersion: Int,
		nodeAId: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "objTest",
				"id" to id,
				"attributes" to mapOf(
					"shortText" to UPDATED_SHORT_TEXT,
					"longText" to UPDATED_LONG_TEXT,
					"integer" to UPDATED_INT,
					"nr" to UPDATED_NR,
					"date" to UPDATED_DATE,
					"isDone" to UPDATED_IS_DONE,
					"testType" to mapOf("id" to UPDATED_TYPE_ID, "name" to "Type B"),
					"nodeList" to listOf(
						mapOf(
							// Include ID -> triggers UPDATE
							"id" to nodeAId,
							"shortText" to "Node A Updated",
							"integer" to 10,
						),
						mapOf(
							// No ID -> triggers INSERT
							"shortText" to "Node C",
							"integer" to 30,
						),
						// Node B omitted -> triggers DELETE
					),
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	@Suppress("UNCHECKED_CAST")
	private fun parseJsonApiResponse(json: String): Map<String, Any?> {
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

	private fun verifyEnumField(
		actual: Any?,
		expectedId: String,
	) {
		assertNotNull(actual, "Enum field should not be null")
		when (actual) {
			is Map<*, *> -> assertEquals(expectedId, actual["id"], "Enum id should match")
			is String -> assertEquals(expectedId, actual, "Enum id should match")
			else -> throw AssertionError("Unexpected enum field type: ${actual?.javaClass}")
		}
	}

	private fun verifyBigDecimal(
		actual: Any?,
		expected: BigDecimal,
	) {
		assertNotNull(actual, "BigDecimal field should not be null")
		val actualBigDecimal =
			when (actual) {
				is BigDecimal -> actual
				is Number -> BigDecimal.valueOf(actual.toDouble())
				is String -> BigDecimal(actual)
				else -> throw AssertionError("Unexpected BigDecimal field type: ${actual?.javaClass}")
			}
		assertEquals(0, expected.compareTo(actualBigDecimal), "BigDecimal values should match")
	}

	/** Verify attributes match the initial (pre-update) values. */
	private fun verifyInitialAttributes(attributes: Map<*, *>) {
		assertEquals(TEST_SHORT_TEXT, attributes["shortText"])
		assertEquals(TEST_LONG_TEXT, attributes["longText"])
		assertEquals(TEST_INT, attributes["integer"])
		verifyBigDecimal(attributes["nr"], BigDecimal.valueOf(TEST_NR))
		assertEquals(TEST_DATE, attributes["date"])
		assertEquals(TEST_IS_DONE, attributes["isDone"])
		verifyEnumField(attributes["testType"], TEST_TYPE_ID)
	}

	/** Verify attributes match the updated values. */
	private fun verifyUpdatedAttributes(attributes: Map<*, *>) {
		assertEquals(UPDATED_SHORT_TEXT, attributes["shortText"])
		assertEquals(UPDATED_LONG_TEXT, attributes["longText"])
		assertEquals(UPDATED_INT, attributes["integer"])
		verifyBigDecimal(attributes["nr"], BigDecimal.valueOf(UPDATED_NR))
		assertEquals(UPDATED_DATE, attributes["date"])
		assertEquals(UPDATED_IS_DONE, attributes["isDone"])
		verifyEnumField(attributes["testType"], UPDATED_TYPE_ID)
	}

	/**
	 * Verify the initial nodeList has 2 nodes (Node A and Node B).
	 * @return Pair of (nodeAId, nodeBId) for use in update verification
	 */
	private fun verifyInitialNodeList(nodeList: List<*>): Pair<String, String> {
		assertEquals(2, nodeList.size, "Initial nodeList should have 2 nodes")
		val nodeA = nodeList.find { (it as Map<*, *>)["shortText"] == "Node A" } as Map<*, *>
		val nodeB = nodeList.find { (it as Map<*, *>)["shortText"] == "Node B" } as Map<*, *>
		assertNotNull(nodeA, "Node A should exist")
		assertNotNull(nodeB, "Node B should exist")
		assertEquals(1, nodeA["integer"], "Node A integer should be 1")
		assertEquals(2, nodeB["integer"], "Node B integer should be 2")
		val nodeAId = nodeA["id"] as String
		val nodeBId = nodeB["id"] as String
		assertNotNull(nodeAId, "Node A should have an ID")
		assertNotNull(nodeBId, "Node B should have an ID")
		return Pair(nodeAId, nodeBId)
	}

	/**
	 * Verify the updated nodeList:
	 * - Node A was UPDATED (same ID, new values)
	 * - Node B was DELETED (ID no longer present)
	 * - Node C was INSERTED (new ID)
	 */
	private fun verifyUpdatedNodeList(
		nodeList: List<*>,
		expectedNodeAId: String,
		deletedNodeBId: String,
	) {
		assertEquals(2, nodeList.size, "Updated nodeList should have 2 nodes")
		val nodeById = nodeList.associateBy { (it as Map<*, *>)["id"] as String }

		// Verify Node A was UPDATED (same ID, new values)
		val updatedNodeA = nodeById[expectedNodeAId]
		assertNotNull(updatedNodeA, "Node A should still exist with same ID (update)")
		assertEquals(
			"Node A Updated",
			(updatedNodeA as Map<*, *>)["shortText"],
			"Node A shortText should be updated",
		)
		assertEquals(10, updatedNodeA["integer"], "Node A integer should be updated to 10")

		// Verify Node B was DELETED (ID no longer present)
		assertNull(nodeById[deletedNodeBId], "Node B should be deleted (ID not present)")

		// Verify Node C was INSERTED (new ID, not matching A or B)
		val nodeC = nodeList.find { (it as Map<*, *>)["id"] != expectedNodeAId } as Map<*, *>
		assertNotEquals(
			deletedNodeBId,
			nodeC["id"],
			"Node C should have a new ID (not Node B's old ID)",
		)
		assertEquals("Node C", nodeC["shortText"], "Node C shortText should be 'Node C'")
		assertEquals(30, nodeC["integer"], "Node C integer should be 30")
	}

	/**
	 * Verify tenant info is present in the meta section.
	 *
	 * The tenant info is always included in meta for all aggregates,
	 * providing id, name, and itemType.
	 */
	@Suppress("UNCHECKED_CAST")
	private fun verifyTenantInMeta(meta: Map<*, *>) {
		val tenantMeta = meta["tenant"] as? Map<String, Any?>
		assertNotNull(tenantMeta, "Meta should have tenant info")

		assertEquals("3", tenantMeta!!["id"], "Tenant id should be '3'")
		assertEquals("Test", tenantMeta["name"], "Tenant name should be 'Test'")

		// Verify itemType is present
		val itemType = tenantMeta["itemType"] as? Map<String, Any?>
		assertNotNull(itemType, "Tenant should have itemType")
		assertEquals("obj_tenant", itemType!!["id"], "Tenant itemType id should be 'obj_tenant'")
	}

	/**
	 * Verify tenantInfo relationship and included tenant aggregate.
	 *
	 * When ?include=tenantInfo is used, the response should contain:
	 * - A relationships.tenantInfo section with a link to the tenant
	 * - An included array with the full tenant resource
	 */
	@Suppress("UNCHECKED_CAST")
	private fun verifyTenantRelationAndInclude(response: Map<String, Any?>) {
		// Verify relationships section exists
		val relationships = response["relationships"] as? Map<String, Any?>
		assertNotNull(relationships, "Response should have relationships")

		// Verify tenantInfo relationship
		val tenantInfoRel = relationships!!["tenantInfo"] as? Map<String, Any?>
		assertNotNull(tenantInfoRel, "Relationships should have tenantInfo")

		val tenantInfoData = tenantInfoRel!!["data"] as? Map<String, Any?>
		assertNotNull(tenantInfoData, "tenantInfo should have data")
		assertEquals("tenant", tenantInfoData!!["type"], "tenantInfo type should be 'tenant'")
		assertEquals("3", tenantInfoData["id"], "tenantInfo id should be '3'")

		// Verify included array contains the tenant
		val included = response["included"] as? List<Map<String, Any?>>
		assertNotNull(included, "Response should have included array")
		assertTrue(included!!.isNotEmpty(), "Included array should not be empty")

		// Find the tenant in included array
		val includedTenant = included.find { it["type"] == "tenant" && it["id"] == "3" }
		assertNotNull(includedTenant, "Included array should contain tenant with id=3")

		// Verify tenant attributes
		val tenantAttributes = includedTenant!!["attributes"] as? Map<String, Any?>
		assertNotNull(tenantAttributes, "Included tenant should have attributes")
		assertEquals("test", tenantAttributes!!["key"], "Tenant key should be 'test'")
		assertEquals("Test", tenantAttributes["name"], "Tenant name should be 'Test'")
		verifyEnumField(tenantAttributes["tenantType"], "advisor")
	}
}
