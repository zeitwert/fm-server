package io.zeitwert.fm

import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class TenantTest {

	@Autowired
	private lateinit var tenantRepository: ObjTenantRepository

	@Test
	fun testTenantBase() {
		assertNotNull(tenantRepository, "tenantRepository not null")
		assertEquals("obj_tenant", tenantRepository.aggregateType.id)

		val tenant = tenantRepository.create()
		assertNotNull(tenant, "tenant not null")
		assertNotNull(tenant.id, "id not null")
		assertNotNull(tenant.tenantId, "tenantId not null")

		tenant.name = "Test Tenant"
		tenant.tenantType = CodeTenantType.COMMUNITY
		tenant.description = "Test description"

		tenantRepository.transaction {
			tenantRepository.store(tenant)
		}

		val tenantId = tenant.id
		val loadedTenant = tenantRepository.load(tenantId)

		assertEquals("Test Tenant", loadedTenant.name)
		assertEquals(CodeTenantType.COMMUNITY, loadedTenant.tenantType)
		assertEquals("Test description", loadedTenant.description)
	}

	@Test
	fun testTenantGetByKey() {
		val uniqueKey = "tenant-key-${UUID.randomUUID().toString().substring(0, 8)}"

		// Create tenant with unique key
		val tenant = tenantRepository.create()
		tenant.key = uniqueKey
		tenant.name = "Tenant with key"
		tenant.tenantType = CodeTenantType.COMMUNITY

		tenantRepository.transaction {
			tenantRepository.store(tenant)
		}

		val tenantId = tenant.id

		// Verify getByKey returns the correct tenant
		val foundTenant = tenantRepository.getByKey(uniqueKey)
		assertTrue(foundTenant.isPresent, "tenant should be found by key")
		assertEquals(tenantId, foundTenant.get().id, "found tenant should have correct id")
		assertEquals(uniqueKey, foundTenant.get().key, "found tenant should have correct key")

		// Verify getByKey returns empty for non-existent key
		val notFound = tenantRepository.getByKey("non-existent-key-${UUID.randomUUID()}")
		assertFalse(notFound.isPresent, "non-existent key should return empty")
	}

	@Test
	fun testTenantKeyPersistence() {
		val uniqueKey = "persist-key-${UUID.randomUUID().toString().substring(0, 8)}"

		// Create and store tenant with key
		val tenant = tenantRepository.create()
		tenant.key = uniqueKey
		tenant.name = "Tenant for persistence test"
		tenant.tenantType = CodeTenantType.COMMUNITY

		tenantRepository.transaction {
			tenantRepository.store(tenant)
		}

		val tenantId = tenant.id

		// Load tenant and verify key is persisted
		val loadedTenant = tenantRepository.load(tenantId)
		assertEquals(uniqueKey, loadedTenant.key, "key should be persisted")

		// Update key
		val newKey = "updated-key-${UUID.randomUUID().toString().substring(0, 8)}"
		loadedTenant.key = newKey

		tenantRepository.transaction {
			tenantRepository.store(loadedTenant)
		}

		// Verify updated key
		val reloadedTenant = tenantRepository.load(tenantId)
		assertEquals(newKey, reloadedTenant.key, "updated key should be persisted")

		// Verify old key no longer works
		val oldKeySearch = tenantRepository.getByKey(uniqueKey)
		assertFalse(oldKeySearch.isPresent, "old key should not find tenant")

		// Verify new key works
		val newKeySearch = tenantRepository.getByKey(newKey)
		assertTrue(newKeySearch.isPresent, "new key should find tenant")
		assertEquals(tenantId, newKeySearch.get().id, "new key should find correct tenant")
	}

}
