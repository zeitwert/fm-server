package io.zeitwert.c_api

import com.fasterxml.jackson.databind.ObjectMapper
import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.config.session.TestSessionContext
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
class ObjTenantUserApiTest {

	companion object {

		@JvmStatic
		@BeforeAll
		fun resetRepositoryDirectory() {
			RepositoryDirectory.Companion.resetProvidersAndRepositories()
		}

		private const val TENANT_API_PATH = "/api/oe/tenants"
		private const val USER_API_PATH = "/api/oe/users"

		private const val TENANT_KEY = "api-tenant"
		private const val TENANT_NAME = "API Tenant"
		private const val TENANT_DESCRIPTION = "Tenant created via JSON API"
		private const val TENANT_TYPE_ID = "advisor"
		private val TENANT_INFLATION = BigDecimal("1.5")
		private val TENANT_DISCOUNT = BigDecimal("2.0")

		private const val UPDATED_TENANT_NAME = "API Tenant Updated"
		private const val UPDATED_TENANT_DESCRIPTION = "Updated tenant description"
		private const val UPDATED_TENANT_TYPE_ID = "community"
		private val UPDATED_TENANT_INFLATION = BigDecimal("1.8")
		private val UPDATED_TENANT_DISCOUNT = BigDecimal("2.2")

		private const val USER_EMAIL = "api.user@zeitwert.io"
		private const val USER_PASSWORD = "api-user-pass"
		private const val USER_NAME = "API User"
		private const val USER_DESCRIPTION = "User created via JSON API"
		private const val USER_ROLE_ID = "admin"
		private const val USER_NEED_PASSWORD_CHANGE = false

		private const val UPDATED_USER_NAME = "API User Updated"
		private const val UPDATED_USER_DESCRIPTION = "Updated user description"
		private const val UPDATED_USER_ROLE_ID = "user"

		private lateinit var tenantId: String
		private lateinit var userId: String
		private var tenantVersion: Int = 0
		private var userVersion: Int = 0
	}

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Test
	@Order(1)
	fun `create tenant via JSON API`() {
		val payload = createTenantPayload(
			key = TENANT_KEY,
			name = TENANT_NAME,
			description = TENANT_DESCRIPTION,
			tenantTypeId = TENANT_TYPE_ID,
			inflationRate = TENANT_INFLATION,
			discountRate = TENANT_DISCOUNT,
		)
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(TENANT_API_PATH)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		tenantId = response["id"] as String
		Assertions.assertNotNull(tenantId, "Created tenant should have an ID")

		TestSessionContext.startOverride()
		TestSessionContext.overrideTenantId(tenantId.toInt())
		TestSessionContext.overrideAccountId(null)

		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyTenantAttributes(
			attributes,
			TENANT_KEY,
			TENANT_NAME,
			TENANT_DESCRIPTION,
			TENANT_TYPE_ID,
			TENANT_INFLATION,
			TENANT_DISCOUNT,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		tenantVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(
			meta,
			expectedTenantId = tenantId,
			expectedTenantName = TENANT_NAME,
		)
	}

	@Test
	@Order(2)
	fun `read tenant after create`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$TENANT_API_PATH/$tenantId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyTenantAttributes(
			attributes,
			TENANT_KEY,
			TENANT_NAME,
			TENANT_DESCRIPTION,
			TENANT_TYPE_ID,
			TENANT_INFLATION,
			TENANT_DISCOUNT,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		tenantVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(
			meta,
			expectedTenantId = tenantId,
			expectedTenantName = TENANT_NAME,
		)
	}

	@Test
	@Order(3)
	fun `update tenant via JSON API`() {
		val payload = createTenantUpdatePayload(tenantId, tenantVersion)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$TENANT_API_PATH/$tenantId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyTenantAttributes(
			attributes,
			TENANT_KEY,
			UPDATED_TENANT_NAME,
			UPDATED_TENANT_DESCRIPTION,
			UPDATED_TENANT_TYPE_ID,
			UPDATED_TENANT_INFLATION,
			UPDATED_TENANT_DISCOUNT,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		val newVersion = JsonApiTestSupport.extractVersion(meta)
		Assertions.assertTrue(newVersion > tenantVersion, "Tenant version should increment after update")
		tenantVersion = newVersion
	}

	@Test
	@Order(4)
	fun `create user via JSON API`() {
		val payload = createUserPayload(
			email = USER_EMAIL,
			password = USER_PASSWORD,
			name = USER_NAME,
			description = USER_DESCRIPTION,
			roleId = USER_ROLE_ID,
			needPasswordChange = USER_NEED_PASSWORD_CHANGE,
			tenantId = tenantId,
		)
		val createResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.post(USER_API_PATH)
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isCreated)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, createResult.response.contentAsString)
		userId = response["id"] as String
		Assertions.assertNotNull(userId, "Created user should have an ID")

		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyUserAttributes(
			attributes,
			USER_EMAIL,
			USER_NAME,
			USER_DESCRIPTION,
			USER_ROLE_ID,
			USER_NEED_PASSWORD_CHANGE,
			tenantId,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		userVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TENANT_NAME)
	}

	@Test
	@Order(5)
	fun `read user with tenantInfo include`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.get("$USER_API_PATH/$userId?include=tenantInfo")
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyUserAttributes(
			attributes,
			USER_EMAIL,
			USER_NAME,
			USER_DESCRIPTION,
			USER_ROLE_ID,
			USER_NEED_PASSWORD_CHANGE,
			tenantId,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		userVersion = JsonApiTestSupport.extractVersion(meta)
		JsonApiTestSupport.verifyTenantInMeta(meta, tenantId, TENANT_NAME)
		JsonApiTestSupport.verifyTenantRelationAndInclude(
			response = response,
			expectedTenantId = tenantId,
			expectedTenantKey = TENANT_KEY,
			expectedTenantName = UPDATED_TENANT_NAME,
			expectedTenantTypeId = UPDATED_TENANT_TYPE_ID,
		)
	}

	@Test
	@Order(6)
	fun `update user via JSON API`() {
		val payload = createUserUpdatePayload(userId, userVersion)
		val updateResult = mockMvc
			.perform(
				MockMvcRequestBuilders
					.patch("$USER_API_PATH/$userId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.contentType(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE)
					.content(payload),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, updateResult.response.contentAsString)
		val attributes = JsonApiTestSupport.requireAttributes(response)
		verifyUserAttributes(
			attributes,
			USER_EMAIL,
			UPDATED_USER_NAME,
			UPDATED_USER_DESCRIPTION,
			UPDATED_USER_ROLE_ID,
			USER_NEED_PASSWORD_CHANGE,
			tenantId,
		)

		val meta = JsonApiTestSupport.requireMeta(response)
		val newVersion = JsonApiTestSupport.extractVersion(meta)
		Assertions.assertTrue(newVersion > userVersion, "User version should increment after update")
		userVersion = newVersion
	}

	@Test
	@Order(7)
	fun `delete user via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$USER_API_PATH/$userId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(8)
	fun `read user after delete`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$USER_API_PATH/$userId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val meta = JsonApiTestSupport.requireMeta(response)
		JsonApiTestSupport.verifyClosedMeta(meta)
	}

	@Test
	@Order(9)
	fun `delete tenant via JSON API`() {
		mockMvc
			.perform(
				MockMvcRequestBuilders
					.delete("$TENANT_API_PATH/$tenantId")
					.with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())
					.accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isNoContent)
	}

	@Test
	@Order(10)
	fun `read tenant after delete`() {
		val readResult = mockMvc
			.perform(
				MockMvcRequestBuilders.get("$TENANT_API_PATH/$tenantId").accept(JsonApiTestSupport.JSON_API_CONTENT_TYPE),
			).andExpect(MockMvcResultMatchers.status().isOk)
			.andReturn()

		val response = JsonApiTestSupport.parseJsonApiResponse(objectMapper, readResult.response.contentAsString)
		val meta = JsonApiTestSupport.requireMeta(response)
		JsonApiTestSupport.verifyClosedMeta(meta)
	}

	private fun createTenantPayload(
		key: String,
		name: String,
		description: String,
		tenantTypeId: String,
		inflationRate: BigDecimal,
		discountRate: BigDecimal,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "tenant",
				"attributes" to mapOf(
					"key" to key,
					"name" to name,
					"description" to description,
					"tenantType" to mapOf("id" to tenantTypeId),
					"inflationRate" to inflationRate,
					"discountRate" to discountRate,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createTenantUpdatePayload(
		id: String,
		clientVersion: Int,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "tenant",
				"id" to id,
				"attributes" to mapOf(
					"key" to TENANT_KEY,
					"name" to UPDATED_TENANT_NAME,
					"description" to UPDATED_TENANT_DESCRIPTION,
					"tenantType" to mapOf("id" to UPDATED_TENANT_TYPE_ID),
					"inflationRate" to UPDATED_TENANT_INFLATION,
					"discountRate" to UPDATED_TENANT_DISCOUNT,
				),
				"meta" to mapOf(
					"clientVersion" to clientVersion,
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun verifyTenantAttributes(
		attributes: Map<String, Any?>,
		expectedKey: String,
		expectedName: String,
		expectedDescription: String,
		expectedTenantTypeId: String,
		expectedInflationRate: BigDecimal,
		expectedDiscountRate: BigDecimal,
	) {
		Assertions.assertEquals(expectedKey, attributes["key"])
		Assertions.assertEquals(expectedName, attributes["name"])
		Assertions.assertEquals(expectedDescription, attributes["description"])
		JsonApiTestSupport.verifyEnumField(attributes["tenantType"], expectedTenantTypeId)
		JsonApiTestSupport.verifyBigDecimal(attributes["inflationRate"], expectedInflationRate)
		JsonApiTestSupport.verifyBigDecimal(attributes["discountRate"], expectedDiscountRate)
	}

	private fun createUserPayload(
		email: String,
		password: String,
		name: String,
		description: String,
		roleId: String,
		needPasswordChange: Boolean,
		tenantId: String,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "user",
				"attributes" to mapOf(
					"email" to email,
					"password" to password,
					"needPasswordChange" to needPasswordChange,
					"name" to name,
					"description" to description,
					"role" to mapOf("id" to roleId),
					"tenants" to listOf(
						mapOf(
							"id" to tenantId,
							"name" to TENANT_NAME,
						),
					),
				),
			),
		)
		return objectMapper.writeValueAsString(payload)
	}

	private fun createUserUpdatePayload(
		id: String,
		clientVersion: Int,
	): String {
		val payload = mapOf(
			"data" to mapOf(
				"type" to "user",
				"id" to id,
				"attributes" to mapOf(
					"email" to USER_EMAIL,
					"name" to UPDATED_USER_NAME,
					"description" to UPDATED_USER_DESCRIPTION,
					"role" to mapOf("id" to UPDATED_USER_ROLE_ID),
					"tenants" to listOf(
						mapOf(
							"id" to tenantId,
							"name" to TENANT_NAME,
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

	private fun verifyUserAttributes(
		attributes: Map<String, Any?>,
		expectedEmail: String,
		expectedName: String,
		expectedDescription: String,
		expectedRoleId: String,
		expectedNeedPasswordChange: Boolean,
		expectedTenantId: String,
	) {
		Assertions.assertEquals(expectedEmail, attributes["email"])
		Assertions.assertEquals(expectedName, attributes["name"])
		Assertions.assertEquals(expectedDescription, attributes["description"])
		Assertions.assertEquals(expectedNeedPasswordChange, attributes["needPasswordChange"])
		JsonApiTestSupport.verifyEnumField(attributes["role"], expectedRoleId)
		JsonApiTestSupport.verifyEnumeratedListContains(attributes["tenants"], expectedTenantId)
	}

}
