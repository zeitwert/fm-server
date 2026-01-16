package io.zeitwert.c_api

import com.fasterxml.jackson.databind.ObjectMapper
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.ResourceRepository
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.config.session.TestSessionContext
import io.zeitwert.data.config.TestDataSetup
import io.zeitwert.fm.building.adapter.jsonapi.impl.ObjBuildingDtoAdapter
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
class ObjBuildingApiTest {

	companion object {

		@JvmStatic
		@BeforeAll
		fun resetRepositoryDirectory() {
			RepositoryDirectory.Companion.resetProvidersAndRepositories()
		}

		private const val ACCOUNT_API_PATH = "/api/account/accounts"
		private const val CONTACT_API_PATH = "/api/contact/contacts"
		private const val BUILDING_API_PATH = "/api/building/buildings"

		private const val ACCOUNT_KEY = "api-building-account"
		private const val ACCOUNT_NAME = "API Building Account"
		private const val ACCOUNT_DESCRIPTION = "Account for building API tests"
		private const val ACCOUNT_TYPE_ID = "prospect"
		private const val ACCOUNT_SEGMENT_ID = "community"
		private const val ACCOUNT_CURRENCY_ID = "chf"
		private val ACCOUNT_INFLATION = BigDecimal("1.2")
		private val ACCOUNT_DISCOUNT = BigDecimal("2.0")

		private const val CONTACT_KEY = "api-building-contact"
		private const val CONTACT_ROLE_ID = "councilor"
		private const val CONTACT_SALUTATION_ID = "mr"
		private const val CONTACT_TITLE_ID = "dr"
		private const val CONTACT_FIRST_NAME = "Hans"
		private const val CONTACT_LAST_NAME = "Muster"
		private const val CONTACT_BIRTH_DATE = "1985-05-12"
		private const val CONTACT_PHONE = "044 123 45 67"
		private const val CONTACT_MOBILE = "079 123 45 67"
		private const val CONTACT_EMAIL = "api.building.contact@zeitwert.io"
		private const val CONTACT_DESCRIPTION = "Building contact"

		private const val MAIL_ADDRESS_CHANNEL_ID = "mail"
		private const val MAIL_NAME = "Headquarters"
		private const val MAIL_STREET = "Hauptstrasse 1"
		private const val MAIL_ZIP = "8000"
		private const val MAIL_CITY = "Zurich"
		private const val MAIL_COUNTRY_ID = "ch"

		private const val EMAIL_ADDRESS_CHANNEL_ID = "email"
		private const val EMAIL_NAME = "Primary Email"
		private const val EMAIL_STREET = "Mailweg 2"
		private const val EMAIL_ZIP = "8001"
		private const val EMAIL_CITY = "Zurich"
		private const val EMAIL_COUNTRY_ID = "ch"

		private const val BUILDING_KEY = "api-building"
		private const val BUILDING_NAME = "API Building"
		private const val BUILDING_DESCRIPTION = "Building created via JSON API"
		private const val BUILDING_NR = "B-101"
		private const val BUILDING_INSURANCE_NR = "INS-2025-1"
		private const val BUILDING_STREET = "Hauptstrasse 10"
		private const val BUILDING_ZIP = "8000"
		private const val BUILDING_CITY = "Zurich"
		private const val BUILDING_COUNTRY_ID = "ch"
		private const val BUILDING_CURRENCY_ID = "chf"
		private const val BUILDING_TYPE_ID = "T01"
		private const val BUILDING_YEAR = 1990
		private val BUILDING_INSURED_VALUE = BigDecimal("1250000")
		private const val BUILDING_INSURED_VALUE_YEAR = 2024

		private const val RATING_PART_CATALOG_ID = "C7"
		private const val RATING_MAINTENANCE_STRATEGY_ID = "N"
		private const val RATING_DATE = "2025-01-10"

		private const val RATING_PART_CATALOG_ELEMENT_COUNT = 22

		private const val ELEMENT_A_PART_ID = "P48"
		private const val ELEMENT_A_WEIGHT = 35
		private const val ELEMENT_A_CONDITION = 75
		private const val ELEMENT_A_RATING_YEAR = 2025
		private const val ELEMENT_A_STRAIN = -1
		private const val ELEMENT_A_STRENGTH = 1
		private const val ELEMENT_A_DESCRIPTION = "Facade needs repaint"
		private const val ELEMENT_A_CONDITION_DESCRIPTION = "Weathering visible"
		private const val ELEMENT_A_MEASURE_DESCRIPTION = "Repaint within 2 years"

		private const val ELEMENT_B_PART_ID = "P57"
		private const val ELEMENT_B_WEIGHT = 9
		private const val ELEMENT_B_CONDITION = 82
		private const val ELEMENT_B_RATING_YEAR = 2025
		private const val ELEMENT_B_STRAIN = 0
		private const val ELEMENT_B_STRENGTH = 1
		private const val ELEMENT_B_DESCRIPTION = "Roof maintenance planned"
		private const val ELEMENT_B_CONDITION_DESCRIPTION = "Minor leaks detected"
		private const val ELEMENT_B_MEASURE_DESCRIPTION = "Seal leaks"

		private const val ELEMENT_C_PART_ID = "P58"
		private const val ELEMENT_C_WEIGHT = 8
		private const val ELEMENT_C_CONDITION = 68
		private const val ELEMENT_C_RATING_YEAR = 2025
		private const val ELEMENT_C_STRAIN = 1
		private const val ELEMENT_C_STRENGTH = 0
		private const val ELEMENT_C_DESCRIPTION = "Interior surfaces worn"
		private const val ELEMENT_C_CONDITION_DESCRIPTION = "Scratches and fading"
		private const val ELEMENT_C_MEASURE_DESCRIPTION = "Refinish surfaces"

		private lateinit var tenantId: String
		private lateinit var accountId: String
		private lateinit var contactId: String
		private lateinit var buildingId: String
		private var buildingVersion: Int = 0

		private lateinit var elementAId: String
		private lateinit var elementBId: String
		private lateinit var elementCId: String
		private var elementAWeight: Int = 0
		private var elementBWeight: Int = 0
		private var elementCWeight: Int = 0
		private val elementIds: MutableList<String> = mutableListOf()

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
	}

	@Test
	@Order(3)
	fun `create contact via JSON API`() {
		val payload = createContactPayload(accountId)
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(CONTACT_API_PATH)
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		contactId = response["id"] as String
		Assertions.assertNotNull(contactId, "Created contact should have an ID")
	}

	@Test
	@Order(4)
	fun `create building via JSON API`() {
		val payload = createBuildingPayload(accountId, contactId)
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
		buildingId = response["id"] as String
		Assertions.assertNotNull(buildingId, "Created building should have an ID")

		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyBuildingAttributes(attributes)

		val relationships = JsonApiTestSupport.requireRelationships(response)
		val account = JsonApiTestSupport.requireRelationshipData(relationships, "account")
		JsonApiTestSupport.verifyRelationshipData(account, "account", accountId)
		val contacts = JsonApiTestSupport.requireRelationshipDataList(relationships, "contacts")
		JsonApiTestSupport.verifyRelationshipListContains(contacts, "contact", contactId)

		val meta = JsonApiTestSupport.requireMeta(response)
		buildingVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TestDataSetup.TEST_TENANT_NAME)
	}

	@Test
	@Order(5)
	fun `read building after create`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$BUILDING_API_PATH/$buildingId?include=account,contacts,coverFoto")
					.session(httpSession)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyBuildingAttributes(attributes)
	}

	@Test
	@Order(6)
	fun `add rating via JSON API`() {
		val payload = createAddRatingPayload(buildingId, buildingVersion)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$BUILDING_API_PATH/$buildingId?include=account,contacts,coverFoto")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		val currentRating = requireCurrentRating(attributes)
		val elements = requireElements(currentRating)
		Assertions.assertTrue(elements.isEmpty(), "New rating should have no elements before partCatalog")

		val meta = JsonApiTestSupport.requireMeta(response)
		buildingVersion = JsonApiTestSupport.extractVersion(meta)

		// Reload to ensure currentRating is available in the session-scoped aggregate
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$BUILDING_API_PATH/$buildingId?include=account,contacts,coverFoto")
					.session(httpSession)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val readResponse = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val readAttributes = JsonApiTestSupport.requireAttributes(readResponse)
		requireCurrentRating(readAttributes)
	}

	@Test
	@Order(7)
	fun `set rating part catalog via JSON API`() {
		val payload = createSetPartCatalogPayload(buildingId, buildingVersion)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$BUILDING_API_PATH/$buildingId?include=account,contacts,coverFoto")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		val currentRating = requireCurrentRating(attributes)

		val elements = requireElements(currentRating)
		Assertions.assertEquals(
			RATING_PART_CATALOG_ELEMENT_COUNT,
			elements.size,
			"Part catalog should populate the expected number of elements",
		)
		elementIds.clear()
		elementIds.addAll(elements.map { it["id"] as String })

		val elementsByPart = elements.associateBy { extractElementPartId(it) }
		val elementA = requireElementByPart(elementsByPart, ELEMENT_A_PART_ID, ELEMENT_A_WEIGHT)
		val elementB = requireElementByPart(elementsByPart, ELEMENT_B_PART_ID, ELEMENT_B_WEIGHT)
		val elementC = requireElementByPart(elementsByPart, ELEMENT_C_PART_ID, ELEMENT_C_WEIGHT)

		elementAId = elementA["id"] as String
		elementBId = elementB["id"] as String
		elementCId = elementC["id"] as String
		elementAWeight = (elementA["weight"] as? Number)?.toInt() ?: 0
		elementBWeight = (elementB["weight"] as? Number)?.toInt() ?: 0
		elementCWeight = (elementC["weight"] as? Number)?.toInt() ?: 0

		val meta = JsonApiTestSupport.requireMeta(response)
		buildingVersion = JsonApiTestSupport.extractVersion(meta)
	}

	@Test
	@Order(8)
	fun `update rating elements via JSON API`() {
		val payload = createUpdateElementsPayload(buildingId, buildingVersion)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$BUILDING_API_PATH/$buildingId?include=account,contacts,coverFoto")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		val currentRating = requireCurrentRating(attributes)

		val elements = requireElements(currentRating)
		Assertions.assertEquals(elementIds.size, elements.size, "Element count should remain unchanged")

		val updatedElementA = elements.firstOrNull { it["description"] == ELEMENT_A_DESCRIPTION }
		Assertions.assertNotNull(updatedElementA, "Updated element A should be present")
		verifyUpdatedElement(
			updatedElementA!!,
			elementAWeight,
			ELEMENT_A_CONDITION,
			ELEMENT_A_RATING_YEAR,
			ELEMENT_A_STRAIN,
			ELEMENT_A_STRENGTH,
			ELEMENT_A_DESCRIPTION,
			ELEMENT_A_CONDITION_DESCRIPTION,
			ELEMENT_A_MEASURE_DESCRIPTION,
		)

		val updatedElementB = elements.firstOrNull { it["description"] == ELEMENT_B_DESCRIPTION }
		Assertions.assertNotNull(updatedElementB, "Updated element B should be present")
		verifyUpdatedElement(
			updatedElementB!!,
			elementBWeight,
			ELEMENT_B_CONDITION,
			ELEMENT_B_RATING_YEAR,
			ELEMENT_B_STRAIN,
			ELEMENT_B_STRENGTH,
			ELEMENT_B_DESCRIPTION,
			ELEMENT_B_CONDITION_DESCRIPTION,
			ELEMENT_B_MEASURE_DESCRIPTION,
		)

		val updatedElementC = elements.firstOrNull { it["description"] == ELEMENT_C_DESCRIPTION }
		Assertions.assertNotNull(updatedElementC, "Updated element C should be present")
		verifyUpdatedElement(
			updatedElementC!!,
			elementCWeight,
			ELEMENT_C_CONDITION,
			ELEMENT_C_RATING_YEAR,
			ELEMENT_C_STRAIN,
			ELEMENT_C_STRENGTH,
			ELEMENT_C_DESCRIPTION,
			ELEMENT_C_CONDITION_DESCRIPTION,
			ELEMENT_C_MEASURE_DESCRIPTION,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		buildingVersion = JsonApiTestSupport.extractVersion(meta)
	}

	@Test
	@Order(9)
	fun `delete building via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$BUILDING_API_PATH/$buildingId")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(10)
	fun `read building after delete`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$BUILDING_API_PATH/$buildingId")
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
	fun `delete contact via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$CONTACT_API_PATH/$contactId")
					.session(httpSession)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(12)
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

	private fun createContactPayload(accountId: String): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "contact",
				"attributes" to mapOf(
					"key" to CONTACT_KEY,
					"contactRole" to mapOf("id" to CONTACT_ROLE_ID),
					"salutation" to mapOf("id" to CONTACT_SALUTATION_ID),
					"title" to mapOf("id" to CONTACT_TITLE_ID),
					"firstName" to CONTACT_FIRST_NAME,
					"lastName" to CONTACT_LAST_NAME,
					"birthDate" to CONTACT_BIRTH_DATE,
					"phone" to CONTACT_PHONE,
					"mobile" to CONTACT_MOBILE,
					"email" to CONTACT_EMAIL,
					"description" to CONTACT_DESCRIPTION,
					"mailAddressList" to listOf(
						mapOf(
							"addressChannel" to mapOf("id" to MAIL_ADDRESS_CHANNEL_ID),
							"name" to MAIL_NAME,
							"street" to MAIL_STREET,
							"zip" to MAIL_ZIP,
							"city" to MAIL_CITY,
							"country" to mapOf("id" to MAIL_COUNTRY_ID),
						),
					),
					"electronicAddressList" to listOf(
						mapOf(
							"addressChannel" to mapOf("id" to EMAIL_ADDRESS_CHANNEL_ID),
							"name" to EMAIL_NAME,
							"street" to EMAIL_STREET,
							"zip" to EMAIL_ZIP,
							"city" to EMAIL_CITY,
							"country" to mapOf("id" to EMAIL_COUNTRY_ID),
						),
					),
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

	private fun createBuildingPayload(
		accountId: String,
		contactId: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "building",
				"attributes" to mapOf(
					"key" to BUILDING_KEY,
					"name" to BUILDING_NAME,
					"description" to BUILDING_DESCRIPTION,
					"buildingNr" to BUILDING_NR,
					"insuranceNr" to BUILDING_INSURANCE_NR,
					"street" to BUILDING_STREET,
					"zip" to BUILDING_ZIP,
					"city" to BUILDING_CITY,
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
					"contacts" to mapOf(
						"data" to listOf(
							mapOf(
								"type" to "contact",
								"id" to contactId,
							),
						),
					),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createAddRatingPayload(
		id: String,
		clientVersion: Int,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "building",
				"id" to id,
				"attributes" to emptyMap<String, Any?>(),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
					"operations" to listOf(ObjBuildingDtoAdapter.ADD_RATING_OPERATION),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createSetPartCatalogPayload(
		id: String,
		clientVersion: Int,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "building",
				"id" to id,
				"attributes" to mapOf(
					"currentRating" to mapOf(
						"partCatalog" to mapOf("id" to RATING_PART_CATALOG_ID),
						"maintenanceStrategy" to mapOf("id" to RATING_MAINTENANCE_STRATEGY_ID),
						"ratingDate" to RATING_DATE,
					),
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
					"operations" to listOf(ResourceRepository.CalculationOnlyOperation),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createUpdateElementsPayload(
		id: String,
		clientVersion: Int,
	): String {
		val elementsPayload = listOf(
			mapOf(
				"id" to elementAId,
				"weight" to elementAWeight,
				"condition" to ELEMENT_A_CONDITION,
				"ratingYear" to ELEMENT_A_RATING_YEAR,
				"strain" to ELEMENT_A_STRAIN,
				"strength" to ELEMENT_A_STRENGTH,
				"description" to ELEMENT_A_DESCRIPTION,
				"conditionDescription" to ELEMENT_A_CONDITION_DESCRIPTION,
				"measureDescription" to ELEMENT_A_MEASURE_DESCRIPTION,
			),
			mapOf(
				"id" to elementBId,
				"weight" to elementBWeight,
				"condition" to ELEMENT_B_CONDITION,
				"ratingYear" to ELEMENT_B_RATING_YEAR,
				"strain" to ELEMENT_B_STRAIN,
				"strength" to ELEMENT_B_STRENGTH,
				"description" to ELEMENT_B_DESCRIPTION,
				"conditionDescription" to ELEMENT_B_CONDITION_DESCRIPTION,
				"measureDescription" to ELEMENT_B_MEASURE_DESCRIPTION,
			),
			mapOf(
				"id" to elementCId,
				"weight" to elementCWeight,
				"condition" to ELEMENT_C_CONDITION,
				"ratingYear" to ELEMENT_C_RATING_YEAR,
				"strain" to ELEMENT_C_STRAIN,
				"strength" to ELEMENT_C_STRENGTH,
				"description" to ELEMENT_C_DESCRIPTION,
				"conditionDescription" to ELEMENT_C_CONDITION_DESCRIPTION,
				"measureDescription" to ELEMENT_C_MEASURE_DESCRIPTION,
			),
		)

		val payload = mapOf(
			"data" to mapOf(
				"type" to "building",
				"id" to id,
				"attributes" to mapOf(
					"currentRating" to mapOf(
						"elements" to buildElementUpdateList(elementsPayload, elementIds),
					),
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun buildElementUpdateList(
		updatedElements: List<Map<String, Any?>>,
		allElementIds: List<String>,
	): List<Map<String, Any?>> {
		val updatedById = updatedElements.associateBy { it["id"] as String }
		return allElementIds.map { elementId ->
			updatedById[elementId] ?: mapOf("id" to elementId)
		}
	}

	private fun verifyBuildingAttributes(attributes: Map<String, Any?>) {
		Assertions.assertEquals(BUILDING_NAME, attributes["name"])
		Assertions.assertEquals(BUILDING_DESCRIPTION, attributes["description"])
		Assertions.assertEquals(BUILDING_NR, attributes["buildingNr"])
		Assertions.assertEquals(BUILDING_INSURANCE_NR, attributes["insuranceNr"])
		Assertions.assertEquals(BUILDING_STREET, attributes["street"])
		Assertions.assertEquals(BUILDING_ZIP, attributes["zip"])
		Assertions.assertEquals(BUILDING_CITY, attributes["city"])
		JsonApiTestSupport.verifyEnumField(attributes["country"], BUILDING_COUNTRY_ID)
		JsonApiTestSupport.verifyEnumField(attributes["currency"], BUILDING_CURRENCY_ID)
		JsonApiTestSupport.verifyEnumField(attributes["buildingType"], BUILDING_TYPE_ID)
		Assertions.assertEquals(BUILDING_YEAR, attributes["buildingYear"])
		JsonApiTestSupport.verifyBigDecimal(attributes["insuredValue"], BUILDING_INSURED_VALUE)
		Assertions.assertEquals(BUILDING_INSURED_VALUE_YEAR, attributes["insuredValueYear"])
	}

	private fun verifyCurrentRatingAttributes(currentRating: Map<String, Any?>) {
		JsonApiTestSupport.verifyEnumField(currentRating["partCatalog"], RATING_PART_CATALOG_ID)
		JsonApiTestSupport.verifyEnumField(currentRating["maintenanceStrategy"], RATING_MAINTENANCE_STRATEGY_ID)
		Assertions.assertEquals(RATING_DATE, currentRating["ratingDate"])
	}

	private fun verifyUpdatedElement(
		element: Map<String, Any?>,
		expectedWeight: Int,
		expectedCondition: Int,
		expectedRatingYear: Int,
		expectedStrain: Int,
		expectedStrength: Int,
		expectedDescription: String,
		expectedConditionDescription: String,
		expectedMeasureDescription: String,
	) {
		Assertions.assertEquals(expectedWeight, (element["weight"] as Number).toInt())
		Assertions.assertEquals(expectedCondition, (element["condition"] as Number).toInt())
		Assertions.assertEquals(expectedRatingYear, (element["ratingYear"] as Number).toInt())
		Assertions.assertEquals(expectedStrain, (element["strain"] as Number).toInt())
		Assertions.assertEquals(expectedStrength, (element["strength"] as Number).toInt())
		Assertions.assertEquals(expectedDescription, element["description"])
		Assertions.assertEquals(expectedConditionDescription, element["conditionDescription"])
		Assertions.assertEquals(expectedMeasureDescription, element["measureDescription"])
	}

	private fun requireCurrentRating(attributes: Map<String, Any?>): Map<String, Any?> {
		val currentRating = attributes["currentRating"] as? Map<String, Any?>
		Assertions.assertNotNull(currentRating, "Current rating should be present")
		return currentRating!!
	}

	private fun requireElements(currentRating: Map<String, Any?>): List<Map<String, Any?>> {
		val elements = currentRating["elements"] as? List<Map<String, Any?>>
		Assertions.assertNotNull(elements, "Current rating should have elements list")
		return elements!!
	}

	private fun extractElementPartId(element: Map<String, Any?>): String {
		val part = element["buildingPart"]
		Assertions.assertNotNull(part, "Element should include buildingPart")
		return when (part) {
			is Map<*, *> -> part["id"] as String
			is String -> part
			else -> throw AssertionError("Unexpected buildingPart type: ${part?.javaClass}")
		}
	}

	private fun requireElementByPart(
		elementsByPart: Map<String, Map<String, Any?>>,
		expectedPartId: String,
		expectedWeight: Int,
	): Map<String, Any?> {
		val element = elementsByPart[expectedPartId]
		Assertions.assertNotNull(element, "Expected element for part $expectedPartId")
		val weight = (element!!["weight"] as? Number)?.toInt() ?: 0
		Assertions.assertEquals(expectedWeight, weight, "Element $expectedPartId weight should match")
		return element
	}
}
