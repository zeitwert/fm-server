package io.dddrive.test

import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.domain.oe.model.ObjUserRepository
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("domain", "mem")
class TenantMemTest {

	@Autowired
	private lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	private lateinit var userRepo: ObjUserRepository

	@Test
	fun testTenantRepository() {
		assertEquals("objTenant", tenantRepo.aggregateType.id)

		val kTenant: ObjTenant = tenantRepo.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY).orElse(null)
		assertNotNull(kTenant, "kTenant")

		val kUser: ObjUser = userRepo.getByEmail(ObjUserRepository.KERNEL_USER_EMAIL).orElse(null)
		assertNotNull(kUser, "kUser")

		val tA1 = tenantRepo.create(kTenant.id, kUser.id, OffsetDateTime.now())
		val tA1Id = tA1.id
		assertEquals(tA1Id, tA1.tenantId, "tenant id")

		tA1.name = "Tenant A"
		tenantRepo.store(tA1, kUser.id, OffsetDateTime.now())

		val tA2 = tenantRepo.get(tA1Id)
		assertNotNull(tA2, "tA2 should not be null")
		assertNotSame(tA1, tA2, "different objs after load")
		assertEquals("Tenant A", tA2?.name, "name")
	}

}
