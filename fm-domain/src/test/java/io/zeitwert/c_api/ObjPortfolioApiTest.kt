package io.zeitwert.c_api

import com.fasterxml.jackson.databind.ObjectMapper
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.config.session.TestSessionContext
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
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@WithMockUser(username = "tt@zeitwert.io", roles = ["USER"])
@TestMethodOrder(OrderAnnotation::class)
class ObjPortfolioApiTest {

	companion object {

		@JvmStatic
		@BeforeAll
		fun resetRepositoryDirectory() {
			RepositoryDirectory.Companion.resetProvidersAndRepositories()
		}

		private const val ACCOUNT_API_PATH = "/api/account/accounts"
		private const val BUILDING_API_PATH = "/api/building/buildings"
		private const val PORTFOLIO_API_PATH = "/api/portfolio/portfolios"

		private const val ACCOUNT_KEY = "api-portfolio-account"
		private const val ACCOUNT_NAME = "API Portfolio Account"
		private const val ACCOUNT_DESCRIPTION = "Account for portfolio API tests"
		private const val ACCOUNT_TYPE_ID = "prospect"
		private const val ACCOUNT_SEGMENT_ID = "community"
		private const val ACCOUNT_CURRENCY_ID = "chf"
		private val ACCOUNT_INFLATION = BigDecimal("1.3")
		private val ACCOUNT_DISCOUNT = BigDecimal("2.2")

		private const val BUILDING_A_KEY = "api-portfolio-building-a"
		private const val BUILDING_A_NAME = "API Building A"
		private const val BUILDING_A_DESCRIPTION = "Building A for portfolio tests"
		private const val BUILDING_A_NR = "PB-101"
		private const val BUILDING_A_INSURANCE_NR = "PB-INS-101"
		private const val BUILDING_A_STREET = "Hauptstrasse 10"
		private const val BUILDING_A_ZIP = "8000"
		private const val BUILDING_A_CITY = "Zurich"

		private const val BUILDING_B_KEY = "api-portfolio-building-b"
		private const val BUILDING_B_NAME = "API Building B"
		private const val BUILDING_B_DESCRIPTION = "Building B for portfolio tests"
		private const val BUILDING_B_NR = "PB-102"
		private const val BUILDING_B_INSURANCE_NR = "PB-INS-102"
		private const val BUILDING_B_STREET = "Hauptstrasse 12"
		private const val BUILDING_B_ZIP = "8001"
		private const val BUILDING_B_CITY = "Zurich"

		private const val BUILDING_C_KEY = "api-portfolio-building-c"
		private const val BUILDING_C_NAME = "API Building C"
		private const val BUILDING_C_DESCRIPTION = "Building C for portfolio tests"
		private const val BUILDING_C_NR = "PB-103"
		private const val BUILDING_C_INSURANCE_NR = "PB-INS-103"
		private const val BUILDING_C_STREET = "Hauptstrasse 14"
		private const val BUILDING_C_ZIP = "8002"
		private const val BUILDING_C_CITY = "Zurich"

		private const val BUILDING_COUNTRY_ID = "ch"
		private const val BUILDING_CURRENCY_ID = "chf"
		private const val BUILDING_TYPE_ID = "T01"
		private const val BUILDING_YEAR = 1995
		private val BUILDING_INSURED_VALUE = BigDecimal("950000")
		private const val BUILDING_INSURED_VALUE_YEAR = 2024

		private const val PORTFOLIO_KEY = "api-portfolio"
		private const val PORTFOLIO_NAME = "API Portfolio"
		private const val PORTFOLIO_DESCRIPTION = "Portfolio created via JSON API"
		private const val PORTFOLIO_NR = "P-101"

		private const val UPDATED_PORTFOLIO_NAME = "API Portfolio Updated"
		private const val UPDATED_PORTFOLIO_DESCRIPTION = "Updated portfolio description"

		private lateinit var tenantId: String
		private lateinit var accountId: String
		private lateinit var buildingAId: String
		private lateinit var buildingBId: String
		private lateinit var buildingCId: String
		private lateinit var portfolioId: String
		private var portfolioVersion: Int = 0

		private val httpSession = MockHttpSession()
	}

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var sessionContext: SessionContext

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Test
	@Order(1)
	fun `init session`() {
		tenantId = sessionContext.tenantId.toString()
		TestSessionContext.startOverride()
		TestSessionContext.overrideTenantId(tenantId.toInt())
		TestSessionContext.overrideAccountId(null)
	}

	@Test
	@Order(2)
	fun `create account via JSON API`() {
		val payload = createAccountPayload()
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(ACCOUNT_API_PATH)
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		accountId = response["id"] as String
		Assertions.assertNotNull(accountId, "Created account should have an ID")

		TestSessionContext.overrideAccountId(accountId.toInt())

		val meta = JsonApiTestSupport.requireMeta(response)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TestDataSetup.TEST_TENANT_NAME)
	}

	@Test
	@Order(3)
	fun `create building A via JSON API`() {
		buildingAId = createBuilding(
			BUILDING_A_KEY,
			BUILDING_A_NAME,
			BUILDING_A_DESCRIPTION,
			BUILDING_A_NR,
			BUILDING_A_INSURANCE_NR,
			BUILDING_A_STREET,
			BUILDING_A_ZIP,
			BUILDING_A_CITY,
		)
	}

	@Test
	@Order(4)
	fun `create building B via JSON API`() {
		buildingBId = createBuilding(
			BUILDING_B_KEY,
			BUILDING_B_NAME,
			BUILDING_B_DESCRIPTION,
			BUILDING_B_NR,
			BUILDING_B_INSURANCE_NR,
			BUILDING_B_STREET,
			BUILDING_B_ZIP,
			BUILDING_B_CITY,
		)
	}

	@Test
	@Order(5)
	fun `create building C via JSON API`() {
		buildingCId = createBuilding(
			BUILDING_C_KEY,
			BUILDING_C_NAME,
			BUILDING_C_DESCRIPTION,
			BUILDING_C_NR,
			BUILDING_C_INSURANCE_NR,
			BUILDING_C_STREET,
			BUILDING_C_ZIP,
			BUILDING_C_CITY,
		)
	}

	@Test
	@Order(6)
	fun `create portfolio via JSON API`() {
		val payload = createPortfolioPayload(accountId, buildingBId, PORTFOLIO_NAME, PORTFOLIO_DESCRIPTION)
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(PORTFOLIO_API_PATH)
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		portfolioId = response["id"] as String
		Assertions.assertNotNull(portfolioId, "Created portfolio should have an ID")

		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyPortfolioAttributes(attributes, PORTFOLIO_NAME, PORTFOLIO_DESCRIPTION)
		verifyBuildingSet(attributes["buildings"], setOf(buildingAId, buildingCId), setOf(buildingBId))

		val meta = JsonApiTestSupport.requireMeta(response)
		portfolioVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TestDataSetup.TEST_TENANT_NAME)
	}

	@Test
	@Order(7)
	fun `read portfolio after create`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$PORTFOLIO_API_PATH/$portfolioId")
					.session(httpSession)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyPortfolioAttributes(attributes, PORTFOLIO_NAME, PORTFOLIO_DESCRIPTION)
		verifyBuildingSet(attributes["buildings"], setOf(buildingAId, buildingCId), setOf(buildingBId))
	}

	@Test
	@Order(8)
	fun `update portfolio exclude building A via JSON API`() {
		val payload = createPortfolioUpdatePayload(
			portfolioId,
			portfolioVersion,
			accountId,
			buildingAId,
			UPDATED_PORTFOLIO_NAME,
			UPDATED_PORTFOLIO_DESCRIPTION,
		)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$PORTFOLIO_API_PATH/$portfolioId")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyPortfolioAttributes(attributes, UPDATED_PORTFOLIO_NAME, UPDATED_PORTFOLIO_DESCRIPTION)
		verifyBuildingSet(attributes["buildings"], setOf(buildingBId, buildingCId), setOf(buildingAId))

		val meta = JsonApiTestSupport.requireMeta(response)
		val newVersion = JsonApiTestSupport.extractVersion(meta)
		Assertions.assertTrue(newVersion > portfolioVersion, "Portfolio version should increment after update")
		portfolioVersion = newVersion
	}

	@Test
	@Order(9)
	fun `delete portfolio via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$PORTFOLIO_API_PATH/$portfolioId")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(10)
	fun `read portfolio after delete`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$PORTFOLIO_API_PATH/$portfolioId")
					.session(httpSession)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val meta = JsonApiTestSupport.requireMeta(response)
		JsonApiTestSupport.verifyClosedMeta(meta)
	}

	@Test
	@Order(11)
	fun `delete building A via JSON API`() {
		deleteBuilding(buildingAId)
	}

	@Test
	@Order(12)
	fun `delete building B via JSON API`() {
		deleteBuilding(buildingBId)
	}

	@Test
	@Order(13)
	fun `delete building C via JSON API`() {
		deleteBuilding(buildingCId)
	}

	@Test
	@Order(14)
	fun `delete account via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$ACCOUNT_API_PATH/$accountId")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	private fun createAccountPayload(): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "account",
				"attributes" to mapOf(
					"key" to ACCOUNT_KEY,
					"name" to ACCOUNT_NAME,
					"description" to ACCOUNT_DESCRIPTION,
					"accountType" to mapOf("id" to ACCOUNT_TYPE_ID),
					"clientSegment" to mapOf("id" to ACCOUNT_SEGMENT_ID),
					"referenceCurrency" to mapOf("id" to ACCOUNT_CURRENCY_ID),
					"inflationRate" to ACCOUNT_INFLATION,
					"discountRate" to ACCOUNT_DISCOUNT,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createBuilding(
		key: String,
		name: String,
		description: String,
		buildingNr: String,
		insuranceNr: String,
		street: String,
		zip: String,
		city: String,
	): String {
		val payload = createBuildingPayload(
			accountId,
			key,
			name,
			description,
			buildingNr,
			insuranceNr,
			street,
			zip,
			city,
		)
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(BUILDING_API_PATH)
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		val buildingId = response["id"] as String
		Assertions.assertNotNull(buildingId, "Created building should have an ID")

		val attributes = JsonApiTestSupport.requireAttributes(response)
		Assertions.assertEquals(name, attributes["name"])

		return buildingId
	}

	private fun deleteBuilding(buildingId: String) {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$BUILDING_API_PATH/$buildingId")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	private fun createBuildingPayload(
		accountId: String,
		key: String,
		name: String,
		description: String,
		buildingNr: String,
		insuranceNr: String,
		street: String,
		zip: String,
		city: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "building",
				"attributes" to mapOf(
					"key" to key,
					"name" to name,
					"description" to description,
					"buildingNr" to buildingNr,
					"insuranceNr" to insuranceNr,
					"street" to street,
					"zip" to zip,
					"city" to city,
					"country" to mapOf("id" to BUILDING_COUNTRY_ID),
					"currency" to mapOf("id" to BUILDING_CURRENCY_ID),
					"buildingType" to mapOf("id" to BUILDING_TYPE_ID),
					"buildingYear" to BUILDING_YEAR,
					"insuredValue" to BUILDING_INSURED_VALUE,
					"insuredValueYear" to BUILDING_INSURED_VALUE_YEAR,
				),
				"relationships" to mapOf(
					"account" to mapOf(
						"data" to mapOf(
							"type" to "account",
							"id" to accountId,
						),
					),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createPortfolioPayload(
		accountId: String,
		excludedBuildingId: String,
		name: String,
		description: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "portfolio",
				"attributes" to mapOf(
					"key" to PORTFOLIO_KEY,
					"name" to name,
					"description" to description,
					"portfolioNr" to PORTFOLIO_NR,
					"includes" to listOf(
						mapOf("id" to accountId),
					),
					"excludes" to listOf(
						mapOf("id" to excludedBuildingId),
					),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createPortfolioUpdatePayload(
		id: String,
		clientVersion: Int,
		accountId: String,
		excludedBuildingId: String,
		name: String,
		description: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "portfolio",
				"id" to id,
				"attributes" to mapOf(
					"key" to PORTFOLIO_KEY,
					"name" to name,
					"description" to description,
					"portfolioNr" to PORTFOLIO_NR,
					"includes" to listOf(
						mapOf("id" to accountId),
					),
					"excludes" to listOf(
						mapOf("id" to excludedBuildingId),
					),
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun verifyPortfolioAttributes(
		attributes: Map<String, Any?>,
		expectedName: String,
		expectedDescription: String,
	) {
		Assertions.assertEquals(expectedName, attributes["name"])
		Assertions.assertEquals(expectedDescription, attributes["description"])
		Assertions.assertEquals(PORTFOLIO_NR, attributes["portfolioNr"])
	}

	private fun verifyBuildingSet(
		buildings: Any?,
		expectedIds: Set<String>,
		excludedIds: Set<String>,
	) {
		Assertions.assertNotNull(buildings, "Portfolio should expose buildings list")
		val ids = extractEnumeratedIds(buildings)
		Assertions.assertEquals(expectedIds.size, ids.size, "Building set size should match expected")
		expectedIds.forEach { expectedId ->
			Assertions.assertTrue(ids.contains(expectedId), "Expected building set to contain $expectedId")
		}
		excludedIds.forEach { excludedId ->
			Assertions.assertFalse(ids.contains(excludedId), "Building set should not contain $excludedId")
		}
	}

	private fun extractEnumeratedIds(buildings: Any?): List<String> {
		val list = buildings as? List<*> ?: throw AssertionError("Buildings should be a List")
		return list.mapNotNull { item ->
			when (item) {
				is Map<*, *> -> item["id"] as? String
				is String -> item
				else -> null
			}
		}
	}
}
