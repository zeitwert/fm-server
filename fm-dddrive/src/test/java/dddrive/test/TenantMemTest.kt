package dddrive.test

import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.model.ObjUserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class TenantMemTest {

	@Autowired
	private lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	private lateinit var userRepo: ObjUserRepository

	@Test
	fun testTenantRepository() {
		assertEquals("objTenant", tenantRepo.aggregateType.id)

		val tA1 = tenantRepo.create()
		val tA1Id = tA1.id
		assertEquals(tA1Id, tA1.tenantId, "tenant id")

		tA1.name = "Tenant A"
		tenantRepo.store(tA1)

		val tA2 = tenantRepo.get(tA1Id)
		assertNotNull(tA2, "tA2 should not be null")
		assertNotSame(tA1, tA2, "different objs after load")
		assertEquals("Tenant A", tA2.name, "name")
	}

}
