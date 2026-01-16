package io.zeitwert.c_api

import com.fasterxml.jackson.databind.ObjectMapper
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.ResourceRepository
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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
@TestMethodOrder(OrderAnnotation::class)
class ObjTestApiTest {

	companion object {

		@JvmStatic
		@BeforeAll
		fun resetRepositoryDirectory() {
			// Reset providers/repositories but preserve enumerations for the new test context
			RepositoryDirectory.Companion.resetProvidersAndRepositories()
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

	private const val CALC_ONLY_SHORT_TEXT = "Calc Only Text"

		private lateinit var tenantId: String

		private lateinit var createdId: String
		private lateinit var nodeAId: String
		private lateinit var nodeBId: String
		private var currentVersion: Int = 0
	}

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var sessionContext: SessionContext

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Test
	@Order(1)
	fun `create ObjTest via JSON API`() {
		tenantId = sessionContext.tenantId.toString()
		val createPayload = createObjTestPayload()
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(API_PATH)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JSON_API_CONTENT_TYPE)
					.accept(JSON_API_CONTENT_TYPE)
					.content(createPayload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val createResponse = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		createdId = createResponse["id"] as String
		Assertions.assertNotNull(createdId, "Created object should have an ID")

		// Verify created attributes
		val createAttributes = createResponse["attributes"] as Map<*, *>
		verifyInitialAttributes(createAttributes)

		// Verify nodeList was created with 2 nodes, capture their IDs
		val createNodeList = createAttributes["nodeList"] as List<*>
		val nodes = verifyInitialNodeList(createNodeList)
		nodeAId = nodes.first
		nodeBId = nodes.second

		// Verify meta fields
		val createMeta = createResponse["meta"] as Map<*, *>
		Assertions.assertNotNull(createMeta["version"], "Meta should have version")
		Assertions.assertEquals(1, createMeta["version"])
		Assertions.assertNotNull(createMeta["createdAt"], "Meta should have createdAt")
		Assertions.assertNotNull(createMeta["createdByUser"], "Meta should have createdByUser")
		Assertions.assertNull(createMeta["closedAt"], "closedAt should be null for new object")
		Assertions.assertNull(createMeta["closedByUser"], "closedByUser should be null for new object")
	}

	@Test
	@Order(2)
	fun `read ObjTest with tenantInfo include`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$API_PATH/$createdId?include=tenantInfo").accept(JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val readResponse = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		Assertions.assertEquals(createdId, readResponse["id"])

		// Verify read attributes
		val readAttributes = readResponse["attributes"] as Map<*, *>
		verifyInitialAttributes(readAttributes)

		// Verify nodeList
		val readNodeList = readAttributes["nodeList"] as List<*>
		verifyInitialNodeList(readNodeList)

		val readMeta = readResponse["meta"] as Map<*, *>
		Assertions.assertNull(readMeta["closedAt"], "closedAt should still be null")
		Assertions.assertNull(readMeta["closedByUser"], "closedByUser should still be null")
		currentVersion = readMeta["version"] as Int

		// Verify tenant info is in meta (always present)
		JsonApiTestSupport.verifyTenantInMeta(readMeta, tenantId, TestDataSetup.TEST_TENANT_NAME)

		// Verify tenantInfo relationship and included tenant aggregate
		JsonApiTestSupport.verifyTenantRelationAndInclude(readResponse, tenantId)
	}

	@Test
	@Order(3)
	fun `update ObjTest via JSON API`() {
		// Update will: UPDATE Node A (by ID), INSERT Node C (no ID), DELETE Node B (omitted)
		val updatePayload = createUpdatePayload(createdId, currentVersion, nodeAId)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$API_PATH/$createdId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JSON_API_CONTENT_TYPE)
					.accept(JSON_API_CONTENT_TYPE)
					.content(updatePayload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val updateResponse = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		Assertions.assertEquals(createdId, updateResponse["id"])

		// Verify updated attributes
		val updatedAttributes = updateResponse["attributes"] as Map<*, *>
		verifyUpdatedAttributes(updatedAttributes)

		// Verify nodeList: Node A updated, Node B deleted, Node C inserted
		val updatedNodeList = updatedAttributes["nodeList"] as List<*>
		verifyUpdatedNodeList(updatedNodeList, nodeAId, nodeBId)

		// Verify meta - version should be incremented
		val updatedMeta = updateResponse["meta"] as Map<*, *>
		val newVersion = updatedMeta["version"] as Int
		Assertions.assertTrue(newVersion > currentVersion, "Version should be incremented after update")
		currentVersion = newVersion
		Assertions.assertNotNull(updatedMeta["modifiedAt"], "modifiedAt should be set after update")
		Assertions.assertNotNull(updatedMeta["modifiedByUser"], "modifiedByUser should be set after update")
		Assertions.assertNull(updatedMeta["closedAt"], "closedAt should still be null")
		Assertions.assertNull(updatedMeta["closedByUser"], "closedByUser should still be null")
	}

	@Test
	@Order(4)
	fun `update ObjTest calculationOnly recalculates shortText fields`() {
		val versionBefore = currentVersion
		val calcOnlyPayload = createCalculationOnlyPayload(createdId, versionBefore, CALC_ONLY_SHORT_TEXT)
		val calcOnlyResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$API_PATH/$createdId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JSON_API_CONTENT_TYPE)
					.accept(JSON_API_CONTENT_TYPE)
					.content(calcOnlyPayload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val calcOnlyResponse =
			JsonApiTestSupport.parseJsonApiResponse(objectMapper, calcOnlyResult.response.contentAsString)
		val calcOnlyAttributes = calcOnlyResponse["attributes"] as Map<*, *>
		Assertions.assertEquals(CALC_ONLY_SHORT_TEXT, calcOnlyAttributes["shortText"])
		Assertions.assertEquals(CALC_ONLY_SHORT_TEXT.uppercase(), calcOnlyAttributes["shortTextU"])
		Assertions.assertEquals(CALC_ONLY_SHORT_TEXT.lowercase(), calcOnlyAttributes["shortTextL"])

		val calcOnlyMeta = calcOnlyResponse["meta"] as Map<*, *>
		Assertions.assertEquals(versionBefore, calcOnlyMeta["version"], "Version should not change on calculationOnly")
	}

	@Test
	@Order(5)
	fun `read ObjTest after update`() {
		val readUpdatedResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$API_PATH/$createdId").accept(JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val readUpdatedResponse =
			JsonApiTestSupport.parseJsonApiResponse(objectMapper, readUpdatedResult.response.contentAsString)

		// Verify updated attributes
		val updatedAttributes = readUpdatedResponse["attributes"] as Map<*, *>
		verifyUpdatedAttributes(updatedAttributes)

		// Verify nodeList: Node A updated, Node B deleted, Node C inserted
		val updatedNodeList = updatedAttributes["nodeList"] as List<*>
		verifyUpdatedNodeList(updatedNodeList, nodeAId, nodeBId)

		// Verify meta - version should be incremented
		val updatedMeta = readUpdatedResponse["meta"] as Map<*, *>
		val newVersion = updatedMeta["version"] as Int
		Assertions.assertTrue(newVersion >= currentVersion, "Version should be incremented after update")
		Assertions.assertNotNull(updatedMeta["modifiedAt"], "modifiedAt should be set after update")
		Assertions.assertNotNull(updatedMeta["modifiedByUser"], "modifiedByUser should be set after update")
		Assertions.assertNull(updatedMeta["closedAt"], "closedAt should still be null")
		Assertions.assertNull(updatedMeta["closedByUser"], "closedByUser should still be null")
	}

	@Test
	@Order(6)
	fun `delete ObjTest via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$API_PATH/$createdId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(7)
	fun `read ObjTest after delete`() {
		val readClosedResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$API_PATH/$createdId").accept(JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val readClosedResponse =
			JsonApiTestSupport.parseJsonApiResponse(objectMapper, readClosedResult.response.contentAsString)
		val closedMeta = readClosedResponse["meta"] as Map<*, *>

		Assertions.assertNotNull(closedMeta["closedAt"], "closedAt should be set after delete")
		Assertions.assertNotNull(closedMeta["closedByUser"], "closedByUser should be set after delete")
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

	private fun createCalculationOnlyPayload(
		id: String,
		clientVersion: Int,
		shortText: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "objTest",
				"id" to id,
				"attributes" to mapOf(
					"shortText" to shortText,
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
					"operations" to listOf(ResourceRepository.CalculationOnlyOperation),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	/** Verify attributes match the initial (pre-update) values. */
	private fun verifyInitialAttributes(attributes: Map<*, *>) {
		Assertions.assertEquals(TEST_SHORT_TEXT, attributes["shortText"])
		Assertions.assertEquals(TEST_SHORT_TEXT.uppercase(), attributes["shortTextU"])
		Assertions.assertEquals(TEST_SHORT_TEXT.lowercase(), attributes["shortTextL"])
		Assertions.assertEquals(TEST_LONG_TEXT, attributes["longText"])
		Assertions.assertEquals(TEST_INT, attributes["integer"])
		JsonApiTestSupport.verifyBigDecimal(attributes["nr"], BigDecimal.valueOf(TEST_NR))
		Assertions.assertEquals(TEST_DATE, attributes["date"])
		Assertions.assertEquals(TEST_IS_DONE, attributes["isDone"])
		JsonApiTestSupport.verifyEnumField(attributes["testType"], TEST_TYPE_ID)
	}

	/** Verify attributes match the updated values. */
	private fun verifyUpdatedAttributes(attributes: Map<*, *>) {
		Assertions.assertEquals(UPDATED_SHORT_TEXT, attributes["shortText"])
		Assertions.assertEquals(UPDATED_SHORT_TEXT.uppercase(), attributes["shortTextU"])
		Assertions.assertEquals(UPDATED_SHORT_TEXT.lowercase(), attributes["shortTextL"])
		Assertions.assertEquals(UPDATED_LONG_TEXT, attributes["longText"])
		Assertions.assertEquals(UPDATED_INT, attributes["integer"])
		JsonApiTestSupport.verifyBigDecimal(attributes["nr"], BigDecimal.valueOf(UPDATED_NR))
		Assertions.assertEquals(UPDATED_DATE, attributes["date"])
		Assertions.assertEquals(UPDATED_IS_DONE, attributes["isDone"])
		JsonApiTestSupport.verifyEnumField(attributes["testType"], UPDATED_TYPE_ID)
	}

	/**
	 * Verify the initial nodeList has 2 nodes (Node A and Node B).
	 * @return Pair of (nodeAId, nodeBId) for use in update verification
	 */
	private fun verifyInitialNodeList(nodeList: List<*>): Pair<String, String> {
		Assertions.assertEquals(2, nodeList.size, "Initial nodeList should have 2 nodes")
		val nodeA = nodeList.find { (it as Map<*, *>)["shortText"] == "Node A" } as Map<*, *>
		val nodeB = nodeList.find { (it as Map<*, *>)["shortText"] == "Node B" } as Map<*, *>
		Assertions.assertNotNull(nodeA, "Node A should exist")
		Assertions.assertNotNull(nodeB, "Node B should exist")
		Assertions.assertEquals(1, nodeA["integer"], "Node A integer should be 1")
		Assertions.assertEquals(2, nodeB["integer"], "Node B integer should be 2")
		val nodeAId = nodeA["id"] as String
		val nodeBId = nodeB["id"] as String
		Assertions.assertNotNull(nodeAId, "Node A should have an ID")
		Assertions.assertNotNull(nodeBId, "Node B should have an ID")
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
		Assertions.assertEquals(2, nodeList.size, "Updated nodeList should have 2 nodes")
		val nodeById = nodeList.associateBy { (it as Map<*, *>)["id"] as String }

		// Verify Node A was UPDATED (same ID, new values)
		val updatedNodeA = nodeById[expectedNodeAId]
		Assertions.assertNotNull(updatedNodeA, "Node A should still exist with same ID (update)")
		Assertions.assertEquals(
			"Node A Updated",
			(updatedNodeA as Map<*, *>)["shortText"],
			"Node A shortText should be updated",
		)
		Assertions.assertEquals(10, updatedNodeA["integer"], "Node A integer should be updated to 10")

		// Verify Node B was DELETED (ID no longer present)
		Assertions.assertNull(nodeById[deletedNodeBId], "Node B should be deleted (ID not present)")

		// Verify Node C was INSERTED (new ID, not matching A or B)
		val nodeC = nodeList.find { (it as Map<*, *>)["id"] != expectedNodeAId } as Map<*, *>
		Assertions.assertNotEquals(
			deletedNodeBId,
			nodeC["id"],
			"Node C should have a new ID (not Node B's old ID)",
		)
		Assertions.assertEquals("Node C", nodeC["shortText"], "Node C shortText should be 'Node C'")
		Assertions.assertEquals(30, nodeC["integer"], "Node C integer should be 30")
	}

}
