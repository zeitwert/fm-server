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
class ObjAccountContactApiTest {

	companion object {

		@JvmStatic
		@BeforeAll
		fun resetRepositoryDirectory() {
			RepositoryDirectory.Companion.resetProvidersAndRepositories()
		}

		private const val ACCOUNT_API_PATH = "/api/account/accounts"
		private const val CONTACT_API_PATH = "/api/contact/contacts"

		private const val ACCOUNT_KEY = "api-account"
		private const val ACCOUNT_NAME = "API Account"
		private const val ACCOUNT_DESCRIPTION = "Account created via JSON API"
		private const val ACCOUNT_TYPE_ID = "prospect"
		private const val ACCOUNT_SEGMENT_ID = "community"
		private const val ACCOUNT_CURRENCY_ID = "chf"
		private val ACCOUNT_INFLATION = BigDecimal("1.4")
		private val ACCOUNT_DISCOUNT = BigDecimal("2.1")

		private const val UPDATED_ACCOUNT_NAME = "API Account Updated"
		private const val UPDATED_ACCOUNT_DESCRIPTION = "Updated account description"
		private const val UPDATED_ACCOUNT_TYPE_ID = "client"
		private const val UPDATED_ACCOUNT_SEGMENT_ID = "family"
		private val UPDATED_ACCOUNT_INFLATION = BigDecimal("1.7")
		private val UPDATED_ACCOUNT_DISCOUNT = BigDecimal("2.4")

		private const val CONTACT_KEY = "api-contact"
		private const val CONTACT_ROLE_ID = "councilor"
		private const val CONTACT_SALUTATION_ID = "mr"
		private const val CONTACT_TITLE_ID = "dr"
		private const val CONTACT_FIRST_NAME = "Hans"
		private const val CONTACT_LAST_NAME = "Muster"
		private const val CONTACT_BIRTH_DATE = "1985-05-12"
		private const val CONTACT_PHONE = "044 123 45 67"
		private const val CONTACT_MOBILE = "079 123 45 67"
		private const val CONTACT_EMAIL = "api.contact@zeitwert.io"
		private const val CONTACT_DESCRIPTION = "Contact created via JSON API"

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

		private const val UPDATED_CONTACT_ROLE_ID = "other"
		private const val UPDATED_CONTACT_SALUTATION_ID = "mrs"
		private const val UPDATED_CONTACT_TITLE_ID = "prof"
		private const val UPDATED_CONTACT_FIRST_NAME = "Hans-Peter"
		private const val UPDATED_CONTACT_LAST_NAME = "Musterli"
		private const val UPDATED_CONTACT_BIRTH_DATE = "1980-01-01"
		private const val UPDATED_CONTACT_PHONE = "044 765 43 21"
		private const val UPDATED_CONTACT_MOBILE = "079 765 43 21"
		private const val UPDATED_CONTACT_EMAIL = "api.contact.updated@zeitwert.io"
		private const val UPDATED_CONTACT_DESCRIPTION = "Updated contact description"

		private const val UPDATED_MAIL_NAME = "Branch Office"
		private const val UPDATED_MAIL_STREET = "Bahnhofstrasse 10"
		private const val UPDATED_MAIL_ZIP = "8002"
		private const val UPDATED_MAIL_CITY = "Zurich"
		private const val UPDATED_MAIL_COUNTRY_ID = "ch"

		private const val UPDATED_EMAIL_NAME = "Secondary Email"
		private const val UPDATED_EMAIL_STREET = "Mailweg 3"
		private const val UPDATED_EMAIL_ZIP = "8003"
		private const val UPDATED_EMAIL_CITY = "Zurich"
		private const val UPDATED_EMAIL_COUNTRY_ID = "ch"

		private lateinit var tenantId: String
		private lateinit var accountId: String
		private lateinit var contactId: String
		private var accountVersion: Int = 0
		private var contactVersion: Int = 0
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
		println("sessionContext: ${sessionContext.tenantId}, ${sessionContext.userId}, ${sessionContext.accountId}")
		val payload = createAccountPayload()
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(ACCOUNT_API_PATH)
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

		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyAccountAttributes(
			attributes,
			ACCOUNT_KEY,
			ACCOUNT_NAME,
			ACCOUNT_DESCRIPTION,
			ACCOUNT_TYPE_ID,
			ACCOUNT_SEGMENT_ID,
			ACCOUNT_CURRENCY_ID,
			ACCOUNT_INFLATION,
			ACCOUNT_DISCOUNT,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		accountVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TestDataSetup.TEST_TENANT_NAME)
	}

	@Test
	@Order(3)
	fun `read account after create`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$ACCOUNT_API_PATH/$accountId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyAccountAttributes(
			attributes,
			ACCOUNT_KEY,
			ACCOUNT_NAME,
			ACCOUNT_DESCRIPTION,
			ACCOUNT_TYPE_ID,
			ACCOUNT_SEGMENT_ID,
			ACCOUNT_CURRENCY_ID,
			ACCOUNT_INFLATION,
			ACCOUNT_DISCOUNT,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		accountVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TestDataSetup.TEST_TENANT_NAME)
	}

	@Test
	@Order(4)
	fun `update account via JSON API`() {
		val payload = createAccountUpdatePayload(accountId, accountVersion)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$ACCOUNT_API_PATH/$accountId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyAccountAttributes(
			attributes,
			ACCOUNT_KEY,
			UPDATED_ACCOUNT_NAME,
			UPDATED_ACCOUNT_DESCRIPTION,
			UPDATED_ACCOUNT_TYPE_ID,
			UPDATED_ACCOUNT_SEGMENT_ID,
			ACCOUNT_CURRENCY_ID,
			UPDATED_ACCOUNT_INFLATION,
			UPDATED_ACCOUNT_DISCOUNT,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		val newVersion = JsonApiTestSupport.extractVersion(meta)
		Assertions.assertTrue(newVersion > accountVersion, "Account version should increment after update")
		accountVersion = newVersion
	}

	@Test
	@Order(5)
	fun `create contact via JSON API`() {
		val payload = createContactPayload(accountId)
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(CONTACT_API_PATH)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		contactId = response["id"] as String
		Assertions.assertNotNull(contactId, "Created contact should have an ID")

		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyContactAttributes(
			attributes,
			CONTACT_ROLE_ID,
			CONTACT_SALUTATION_ID,
			CONTACT_TITLE_ID,
			CONTACT_FIRST_NAME,
			CONTACT_LAST_NAME,
			CONTACT_BIRTH_DATE,
			CONTACT_PHONE,
			CONTACT_MOBILE,
			CONTACT_EMAIL,
			CONTACT_DESCRIPTION,
			CONTACT_KEY,
		)
		verifyAddressLists(
			attributes,
			MAIL_ADDRESS_CHANNEL_ID,
			MAIL_NAME,
			MAIL_STREET,
			MAIL_ZIP,
			MAIL_CITY,
			MAIL_COUNTRY_ID,
			EMAIL_ADDRESS_CHANNEL_ID,
			EMAIL_NAME,
			EMAIL_STREET,
			EMAIL_ZIP,
			EMAIL_CITY,
			EMAIL_COUNTRY_ID,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		contactVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TestDataSetup.TEST_TENANT_NAME)
	}

	@Test
	@Order(6)
	fun `set account main contact via JSON API`() {
		val payload = createAccountMainContactPayload(accountId, accountVersion, contactId)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$ACCOUNT_API_PATH/$accountId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val meta = JsonApiTestSupport.requireMeta(response)
		val newVersion = JsonApiTestSupport.extractVersion(meta)
		Assertions.assertTrue(newVersion > accountVersion, "Account version should increment after update")
		accountVersion = newVersion
	}

	@Test
	@Order(7)
	fun `read contact after create`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$CONTACT_API_PATH/$contactId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyContactAttributes(
			attributes,
			CONTACT_ROLE_ID,
			CONTACT_SALUTATION_ID,
			CONTACT_TITLE_ID,
			CONTACT_FIRST_NAME,
			CONTACT_LAST_NAME,
			CONTACT_BIRTH_DATE,
			CONTACT_PHONE,
			CONTACT_MOBILE,
			CONTACT_EMAIL,
			CONTACT_DESCRIPTION,
			CONTACT_KEY,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		contactVersion = JsonApiTestSupport.extractVersion(meta)
	}

	@Test
	@Order(8)
	fun `verify account contact linkage`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$ACCOUNT_API_PATH/$accountId?include=contacts,mainContact")
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val relationships = JsonApiTestSupport.requireRelationships(response)
		val contacts = JsonApiTestSupport.requireRelationshipDataList(relationships, "contacts")
		JsonApiTestSupport.verifyRelationshipListContains(contacts, "contact", contactId)
		val mainContact = JsonApiTestSupport.requireRelationshipData(relationships, "mainContact")
		JsonApiTestSupport.verifyRelationshipData(mainContact, "contact", contactId)
	}

	@Test
	@Order(9)
	fun `update contact via JSON API`() {
		val payload = createContactUpdatePayload(contactId, contactVersion, accountId)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$CONTACT_API_PATH/$contactId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyContactAttributes(
			attributes,
			UPDATED_CONTACT_ROLE_ID,
			UPDATED_CONTACT_SALUTATION_ID,
			UPDATED_CONTACT_TITLE_ID,
			UPDATED_CONTACT_FIRST_NAME,
			UPDATED_CONTACT_LAST_NAME,
			UPDATED_CONTACT_BIRTH_DATE,
			UPDATED_CONTACT_PHONE,
			UPDATED_CONTACT_MOBILE,
			UPDATED_CONTACT_EMAIL,
			UPDATED_CONTACT_DESCRIPTION,
			CONTACT_KEY,
		)
		verifyAddressLists(
			attributes,
			MAIL_ADDRESS_CHANNEL_ID,
			UPDATED_MAIL_NAME,
			UPDATED_MAIL_STREET,
			UPDATED_MAIL_ZIP,
			UPDATED_MAIL_CITY,
			UPDATED_MAIL_COUNTRY_ID,
			EMAIL_ADDRESS_CHANNEL_ID,
			UPDATED_EMAIL_NAME,
			UPDATED_EMAIL_STREET,
			UPDATED_EMAIL_ZIP,
			UPDATED_EMAIL_CITY,
			UPDATED_EMAIL_COUNTRY_ID,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		val newVersion = JsonApiTestSupport.extractVersion(meta)
		Assertions.assertTrue(newVersion > contactVersion, "Contact version should increment after update")
		contactVersion = newVersion
	}

	@Test
	@Order(10)
	fun `delete contact via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$CONTACT_API_PATH/$contactId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(11)
	fun `read contact after delete`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$CONTACT_API_PATH/$contactId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val meta = JsonApiTestSupport.requireMeta(response)
		JsonApiTestSupport.verifyClosedMeta(meta)
	}

	@Test
	@Order(12)
	fun `delete account via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$ACCOUNT_API_PATH/$accountId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(13)
	fun `read account after delete`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$ACCOUNT_API_PATH/$accountId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val meta = JsonApiTestSupport.requireMeta(response)
		JsonApiTestSupport.verifyClosedMeta(meta)
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

	private fun createAccountUpdatePayload(
		id: String,
		clientVersion: Int,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "account",
				"id" to id,
				"attributes" to mapOf(
					"key" to ACCOUNT_KEY,
					"name" to UPDATED_ACCOUNT_NAME,
					"description" to UPDATED_ACCOUNT_DESCRIPTION,
					"accountType" to mapOf("id" to UPDATED_ACCOUNT_TYPE_ID),
					"clientSegment" to mapOf("id" to UPDATED_ACCOUNT_SEGMENT_ID),
					"referenceCurrency" to mapOf("id" to ACCOUNT_CURRENCY_ID),
					"inflationRate" to UPDATED_ACCOUNT_INFLATION,
					"discountRate" to UPDATED_ACCOUNT_DISCOUNT,
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createAccountMainContactPayload(
		id: String,
		clientVersion: Int,
		mainContactId: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "account",
				"id" to id,
				"attributes" to mapOf(
					"key" to ACCOUNT_KEY,
					"name" to UPDATED_ACCOUNT_NAME,
					"description" to UPDATED_ACCOUNT_DESCRIPTION,
					"accountType" to mapOf("id" to UPDATED_ACCOUNT_TYPE_ID),
					"clientSegment" to mapOf("id" to UPDATED_ACCOUNT_SEGMENT_ID),
					"referenceCurrency" to mapOf("id" to ACCOUNT_CURRENCY_ID),
					"inflationRate" to UPDATED_ACCOUNT_INFLATION,
					"discountRate" to UPDATED_ACCOUNT_DISCOUNT,
				),
				"relationships" to mapOf(
					"mainContact" to mapOf(
						"data" to mapOf(
							"type" to "contact",
							"id" to mainContactId,
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

	private fun verifyAccountAttributes(
		attributes: Map<String, Any?>,
		expectedKey: String,
		expectedName: String,
		expectedDescription: String,
		expectedAccountTypeId: String,
		expectedClientSegmentId: String,
		expectedCurrencyId: String,
		expectedInflationRate: BigDecimal,
		expectedDiscountRate: BigDecimal,
	) {
		Assertions.assertEquals(expectedKey, attributes["key"])
		Assertions.assertEquals(expectedName, attributes["name"])
		Assertions.assertEquals(expectedDescription, attributes["description"])
		JsonApiTestSupport.verifyEnumField(attributes["accountType"], expectedAccountTypeId)
		JsonApiTestSupport.verifyEnumField(attributes["clientSegment"], expectedClientSegmentId)
		JsonApiTestSupport.verifyEnumField(attributes["referenceCurrency"], expectedCurrencyId)
		JsonApiTestSupport.verifyBigDecimal(attributes["inflationRate"], expectedInflationRate)
		JsonApiTestSupport.verifyBigDecimal(attributes["discountRate"], expectedDiscountRate)
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

	private fun createContactUpdatePayload(
		id: String,
		clientVersion: Int,
		accountId: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "contact",
				"id" to id,
				"attributes" to mapOf(
					"key" to CONTACT_KEY,
					"contactRole" to mapOf("id" to UPDATED_CONTACT_ROLE_ID),
					"salutation" to mapOf("id" to UPDATED_CONTACT_SALUTATION_ID),
					"title" to mapOf("id" to UPDATED_CONTACT_TITLE_ID),
					"firstName" to UPDATED_CONTACT_FIRST_NAME,
					"lastName" to UPDATED_CONTACT_LAST_NAME,
					"birthDate" to UPDATED_CONTACT_BIRTH_DATE,
					"phone" to UPDATED_CONTACT_PHONE,
					"mobile" to UPDATED_CONTACT_MOBILE,
					"email" to UPDATED_CONTACT_EMAIL,
					"description" to UPDATED_CONTACT_DESCRIPTION,
					"mailAddressList" to listOf(
						mapOf(
							"addressChannel" to mapOf("id" to MAIL_ADDRESS_CHANNEL_ID),
							"name" to UPDATED_MAIL_NAME,
							"street" to UPDATED_MAIL_STREET,
							"zip" to UPDATED_MAIL_ZIP,
							"city" to UPDATED_MAIL_CITY,
							"country" to mapOf("id" to UPDATED_MAIL_COUNTRY_ID),
						),
					),
					"electronicAddressList" to listOf(
						mapOf(
							"addressChannel" to mapOf("id" to EMAIL_ADDRESS_CHANNEL_ID),
							"name" to UPDATED_EMAIL_NAME,
							"street" to UPDATED_EMAIL_STREET,
							"zip" to UPDATED_EMAIL_ZIP,
							"city" to UPDATED_EMAIL_CITY,
							"country" to mapOf("id" to UPDATED_EMAIL_COUNTRY_ID),
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
				"meta" to mapOf(
					"clientVersion" to clientVersion,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun verifyContactAttributes(
		attributes: Map<String, Any?>,
		expectedRoleId: String,
		expectedSalutationId: String,
		expectedTitleId: String,
		expectedFirstName: String,
		expectedLastName: String,
		expectedBirthDate: String,
		expectedPhone: String,
		expectedMobile: String,
		expectedEmail: String,
		expectedDescription: String,
		expectedKey: String,
	) {
		Assertions.assertEquals(expectedKey, attributes["key"])
		JsonApiTestSupport.verifyEnumField(attributes["contactRole"], expectedRoleId)
		JsonApiTestSupport.verifyEnumField(attributes["salutation"], expectedSalutationId)
		JsonApiTestSupport.verifyEnumField(attributes["title"], expectedTitleId)
		Assertions.assertEquals(expectedFirstName, attributes["firstName"])
		Assertions.assertEquals(expectedLastName, attributes["lastName"])
		Assertions.assertEquals(expectedBirthDate, attributes["birthDate"])
		Assertions.assertEquals(expectedPhone, attributes["phone"])
		Assertions.assertEquals(expectedMobile, attributes["mobile"])
		Assertions.assertEquals(expectedEmail, attributes["email"])
		Assertions.assertEquals(expectedDescription, attributes["description"])
	}

	private fun verifyAddressLists(
		attributes: Map<String, Any?>,
		mailChannelId: String,
		mailName: String,
		mailStreet: String,
		mailZip: String,
		mailCity: String,
		mailCountryId: String,
		emailChannelId: String,
		emailName: String,
		emailStreet: String,
		emailZip: String,
		emailCity: String,
		emailCountryId: String,
	) {
		val mailList = attributes["mailAddressList"] as? List<Map<String, Any?>>
		Assertions.assertNotNull(mailList, "mailAddressList should be present")
		val mailAddress = findAddressPart(mailList!!, mailChannelId)
		verifyAddressPart(mailAddress, mailChannelId, mailName, mailStreet, mailZip, mailCity, mailCountryId)

		val emailList = attributes["electronicAddressList"] as? List<Map<String, Any?>>
		Assertions.assertNotNull(emailList, "electronicAddressList should be present")
		val emailAddress = findAddressPart(emailList!!, emailChannelId)
		verifyAddressPart(emailAddress, emailChannelId, emailName, emailStreet, emailZip, emailCity, emailCountryId)
	}

	private fun verifyAddressPart(
		address: Map<String, Any?>,
		expectedChannelId: String,
		expectedName: String,
		expectedStreet: String,
		expectedZip: String,
		expectedCity: String,
		expectedCountryId: String,
	) {
		JsonApiTestSupport.verifyEnumField(address["addressChannel"], expectedChannelId)
		Assertions.assertEquals(expectedName, address["name"])
		Assertions.assertEquals(expectedStreet, address["street"])
		Assertions.assertEquals(expectedZip, address["zip"])
		Assertions.assertEquals(expectedCity, address["city"])
		JsonApiTestSupport.verifyEnumField(address["country"], expectedCountryId)
	}

	private fun findAddressPart(
		addresses: List<Map<String, Any?>>,
		expectedChannelId: String,
	): Map<String, Any?> {
		val match = addresses.firstOrNull { address ->
			when (val channel = address["addressChannel"]) {
				is Map<*, *> -> channel["id"] == expectedChannelId
				is String -> channel == expectedChannelId
				else -> false
			}
		}
		Assertions.assertNotNull(match, "Expected address part for channel $expectedChannelId")
		return match!!
	}
}
