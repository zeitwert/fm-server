package io.zeitwert.jsonapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
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
		val createResult = mockMvc.perform(
			post(API_PATH)
				.with(csrf().asHeader())
				.contentType(JSON_API_CONTENT_TYPE)
				.accept(JSON_API_CONTENT_TYPE)
				.content(createPayload),
		)
			.andExpect(status().isCreated)
			.andReturn()

		val createResponse = parseJsonApiResponse(createResult.response.contentAsString)
		val createdId = createResponse["id"] as String
		assertNotNull(createdId, "Created object should have an ID")

		// Verify created attributes
		val createAttributes = createResponse["attributes"] as Map<*, *>
		assertEquals(TEST_SHORT_TEXT, createAttributes["shortText"])
		assertEquals(TEST_LONG_TEXT, createAttributes["longText"])
		assertEquals(TEST_INT, createAttributes["integer"])
		assertEquals(TEST_DATE, createAttributes["date"])
		assertEquals(TEST_IS_DONE, createAttributes["isDone"])
		verifyEnumField(createAttributes["testType"], TEST_TYPE_ID)

		// Verify nodeList part was created
		val createNodeList = createAttributes["nodeList"] as List<*>
		assertEquals(1, createNodeList.size)
		val createFirstNode = createNodeList[0] as Map<*, *>
		assertEquals("Node A", createFirstNode["shortText"])
		assertEquals(1, createFirstNode["integer"])

		// Verify meta fields
		val createMeta = createResponse["meta"] as Map<*, *>
		assertNotNull(createMeta["version"], "Meta should have version")
		assertEquals(1, createMeta["version"])
		assertNotNull(createMeta["createdAt"], "Meta should have createdAt")
		assertNotNull(createMeta["createdByUser"], "Meta should have createdByUser")
		assertNull(createMeta["closedAt"], "closedAt should be null for new object")
		assertNull(createMeta["closedByUser"], "closedByUser should be null for new object")

		// === READ ===
		val readResult = mockMvc.perform(
			get("$API_PATH/$createdId")
				.accept(JSON_API_CONTENT_TYPE),
		)
			.andExpect(status().isOk)
			.andReturn()

		val readResponse = parseJsonApiResponse(readResult.response.contentAsString)
		assertEquals(createdId, readResponse["id"])

		val readAttributes = readResponse["attributes"] as Map<*, *>
		assertEquals(TEST_SHORT_TEXT, readAttributes["shortText"])
		assertEquals(TEST_LONG_TEXT, readAttributes["longText"])
		assertEquals(TEST_INT, readAttributes["integer"])
		// BigDecimal comparison - nr might be returned as number or string
		verifyBigDecimal(readAttributes["nr"], BigDecimal.valueOf(TEST_NR))
		assertEquals(TEST_DATE, readAttributes["date"])
		assertEquals(TEST_IS_DONE, readAttributes["isDone"])
		verifyEnumField(readAttributes["testType"], TEST_TYPE_ID)

		// Verify nodeList
		val readNodeList = readAttributes["nodeList"] as List<*>
		assertEquals(1, readNodeList.size)

		val readMeta = readResponse["meta"] as Map<*, *>
		assertNull(readMeta["closedAt"], "closedAt should still be null")
		assertNull(readMeta["closedByUser"], "closedByUser should still be null")

		// === UPDATE ===
		val version = readMeta["version"] as Int
		val updatePayload = createUpdatePayload(createdId, version)
		val updateResult = mockMvc.perform(
			patch("$API_PATH/$createdId")
				.with(csrf().asHeader())
				.contentType(JSON_API_CONTENT_TYPE)
				.accept(JSON_API_CONTENT_TYPE)
				.content(updatePayload),
		)
			.andExpect(status().isOk)
			.andReturn()

		val updateResponse = parseJsonApiResponse(updateResult.response.contentAsString)
		assertEquals(createdId, updateResponse["id"])

		// === READ AFTER UPDATE ===
		val readUpdatedResult = mockMvc.perform(
			get("$API_PATH/$createdId")
				.accept(JSON_API_CONTENT_TYPE),
		)
			.andExpect(status().isOk)
			.andReturn()

		val readUpdatedResponse = parseJsonApiResponse(readUpdatedResult.response.contentAsString)
		val updatedAttributes = readUpdatedResponse["attributes"] as Map<*, *>

		// Verify all updated values
		assertEquals(UPDATED_SHORT_TEXT, updatedAttributes["shortText"])
		assertEquals(UPDATED_LONG_TEXT, updatedAttributes["longText"])
		assertEquals(UPDATED_INT, updatedAttributes["integer"])
		verifyBigDecimal(updatedAttributes["nr"], BigDecimal.valueOf(UPDATED_NR))
		assertEquals(UPDATED_DATE, updatedAttributes["date"])
		assertEquals(UPDATED_IS_DONE, updatedAttributes["isDone"])
		verifyEnumField(updatedAttributes["testType"], UPDATED_TYPE_ID)

		// Verify nodeList was updated
		val updatedNodeList = updatedAttributes["nodeList"] as List<*>
		assertEquals(2, updatedNodeList.size)
		val nodeTexts = updatedNodeList.map { (it as Map<*, *>)["shortText"] }
		assertTrue(nodeTexts.contains("Node A Updated"), "Should contain updated node")
		assertTrue(nodeTexts.contains("Node B"), "Should contain new node")

		// Verify meta - version should be incremented
		val updatedMeta = readUpdatedResponse["meta"] as Map<*, *>
		val newVersion = updatedMeta["version"] as Int
		assertTrue(newVersion > version, "Version should be incremented after update")
		assertNotNull(updatedMeta["modifiedAt"], "modifiedAt should be set after update")
		assertNotNull(updatedMeta["modifiedByUser"], "modifiedByUser should be set after update")
		assertNull(updatedMeta["closedAt"], "closedAt should still be null")
		assertNull(updatedMeta["closedByUser"], "closedByUser should still be null")

		// === DELETE (soft delete / close) ===
		mockMvc.perform(
			delete("$API_PATH/$createdId")
				.with(csrf().asHeader())
				.accept(JSON_API_CONTENT_TYPE),
		)
			.andExpect(status().isNoContent)

		// === READ AFTER DELETE (verify closed) ===
		val readClosedResult = mockMvc.perform(
			get("$API_PATH/$createdId")
				.accept(JSON_API_CONTENT_TYPE),
		)
			.andExpect(status().isOk)
			.andReturn()

		val readClosedResponse = parseJsonApiResponse(readClosedResult.response.contentAsString)
		val closedMeta = readClosedResponse["meta"] as Map<*, *>

		assertNotNull(closedMeta["closedAt"], "closedAt should be set after delete")
		assertNotNull(closedMeta["closedByUser"], "closedByUser should be set after delete")

		// Note: UPDATE and DELETE operations have been disabled due to a known framework issue
		// where tenantId is null during the store operation when parts are involved.
		// TODO: Re-enable once the ObjBase.doBeforeStore tenant issue is resolved.
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
					),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createUpdatePayload(
		id: String,
		clientVersion: Int,
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
							"shortText" to "Node A Updated",
							"integer" to 10,
						),
						mapOf(
							"shortText" to "Node B",
							"integer" to 20,
						),
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
		val actualBigDecimal = when (actual) {
			is BigDecimal -> actual
			is Number -> BigDecimal.valueOf(actual.toDouble())
			is String -> BigDecimal(actual)
			else -> throw AssertionError("Unexpected BigDecimal field type: ${actual?.javaClass}")
		}
		assertEquals(0, expected.compareTo(actualBigDecimal), "BigDecimal values should match")
	}
}
