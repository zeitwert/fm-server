package io.dddrive.test

import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.dddrive.property.model.PropertyChangeListener
import io.dddrive.domain.household.model.ObjHouseholdRepository
import io.dddrive.domain.household.model.enums.CodeLabel
import io.dddrive.domain.household.model.enums.CodeSalutation
import io.dddrive.domain.oe.model.ObjTenantRepository
import io.dddrive.domain.oe.model.ObjUserRepository
import io.dddrive.test.server.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("domain", "mem")
class HouseholdMemTest : PropertyChangeListener {
	@Autowired
	private lateinit var tenantRepo: ObjTenantRepository

	@Autowired
	private lateinit var userRepo: ObjUserRepository

	@Autowired
	private lateinit var hhRepo: ObjHouseholdRepository

	private lateinit var tenant: ObjTenant
	private lateinit var user: ObjUser
	private lateinit var user1: ObjUser
	private lateinit var user2: ObjUser

	override fun propertyChange(
		op: String,
		path: String,
		value: String?,
		oldValue: String?,
		isInCalc: Boolean,
	) {
		println("{ op: $op, path: $path, value: $value, oldValue: $oldValue, isInCalc: $isInCalc }")
	}

	@BeforeEach
	fun setUp() {
		tenant = tenantRepo.getByKey(ObjTenantRepository.KERNEL_TENANT_KEY).orElse(null)
		assertNotNull(tenant, "kTenant")

		user = userRepo.getByEmail(ObjUserRepository.KERNEL_USER_EMAIL).orElse(null)
		assertNotNull(user, "kUser")

		user1 =
			userRepo.getByEmail("user1@dfp.ch").orElseGet {
				val newUser = userRepo.create(tenant.id, user.id, OffsetDateTime.now())
				newUser.name = "user1"
				newUser.email = "user1@dfp.ch"
				userRepo.store(newUser, user.id, OffsetDateTime.now())
				newUser
			}
		assertNotNull(user1, "user1")

		user2 =
			userRepo.getByEmail("user2@dfp.ch").orElseGet {
				val newUser = userRepo.create(tenant.id, user.id, OffsetDateTime.now())
				newUser.name = "user2"
				newUser.email = "user2@dfp.ch"
				userRepo.store(newUser, user.id, OffsetDateTime.now())
				newUser
			}
		assertNotNull(user2, "user2")
	}

	@Test
	fun testHouseholdRepository() {
		assertEquals("objHousehold", hhRepo.aggregateType.id)
		assertEquals(0, hhRepo.getByForeignKey("objTypeId", "obj_household").size, "0 hh")

		val hhA1 = hhRepo.create(tenant.id, user.id, OffsetDateTime.now())
		hhA1.meta.addPropertyChangeListener(this) // Assuming meta is not null
		val hhA1Id = hhA1.id
		hhA1.name = "HHA"
		assertEquals(tenant, hhA1.tenant, "tenant")
		assertEquals(user, hhA1.meta.createdByUser, "createUser") // Assuming meta and createdByUser are not null

		// Labels
		assertEquals(0, hhA1.labelSet.size, "labelSet.1a")
		assertFalse(hhA1.hasLabel(CodeLabel.A), "labelSet.1b")
		assertFalse(hhA1.hasLabel(CodeLabel.B), "labelSet.1c")

		hhA1.addLabel(CodeLabel.A)
		assertEquals(1, hhA1.labelSet.size, "labelSet.2a")
		assertTrue(hhA1.hasLabel(CodeLabel.A), "labelSet.2b")
		assertFalse(hhA1.hasLabel(CodeLabel.B), "labelSet.2c")

		hhA1.addLabel(CodeLabel.B)
		assertEquals(2, hhA1.labelSet.size, "labelSet.3a")
		assertTrue(hhA1.hasLabel(CodeLabel.A), "labelSet.3b")
		assertTrue(hhA1.hasLabel(CodeLabel.B), "labelSet.3c")

		hhA1.addLabel(CodeLabel.A) // Adding duplicate
		assertEquals(2, hhA1.labelSet.size, "labelSet.4a")
		assertTrue(hhA1.hasLabel(CodeLabel.A), "labelSet.4b")
		assertTrue(hhA1.hasLabel(CodeLabel.B), "labelSet.4c")

		hhA1.removeLabel(CodeLabel.A)
		assertEquals(1, hhA1.labelSet.size, "labelSet.5a")
		assertFalse(hhA1.hasLabel(CodeLabel.A), "labelSet.5b")
		assertTrue(hhA1.hasLabel(CodeLabel.B), "labelSet.5c")

		hhA1.clearLabelSet()
		assertEquals(0, hhA1.labelSet.size, "labelSet.6a")
		assertFalse(hhA1.hasLabel(CodeLabel.A), "labelSet.6b")
		assertFalse(hhA1.hasLabel(CodeLabel.B), "labelSet.6c")

		hhA1.addLabel(CodeLabel.B)
		assertEquals(1, hhA1.labelSet.size, "labelSet.7a")
		assertFalse(hhA1.hasLabel(CodeLabel.A), "labelSet.7b")
		assertTrue(hhA1.hasLabel(CodeLabel.B), "labelSet.7c")

		// Users
		assertEquals(0, hhA1.userSet.size, "userSet.1a")
		assertFalse(hhA1.hasUser(user1.id!!), "userSet.1b") // Assuming id is non-null after assertNotNull
		assertFalse(hhA1.hasUser(user2.id!!), "userSet.1c")

		hhA1.addUser(user1.id!!)
		assertEquals(1, hhA1.userSet.size, "userSet.2a")
		assertTrue(hhA1.hasUser(user1.id!!), "userSet.2b")
		assertFalse(hhA1.hasUser(user2.id!!), "userSet.2c")

		hhA1.addUser(user2.id!!)
		assertEquals(2, hhA1.userSet.size, "userSet.3a")
		assertTrue(hhA1.hasUser(user1.id!!), "userSet.3b")
		assertTrue(hhA1.hasUser(user2.id!!), "userSet.3c")

		hhA1.addUser(user1.id!!) // Adding duplicate
		assertEquals(2, hhA1.userSet.size, "userSet.4a")
		assertTrue(hhA1.hasUser(user1.id!!), "userSet.4b")
		assertTrue(hhA1.hasUser(user2.id!!), "userSet.4c")

		hhA1.removeUser(user1.id!!)
		assertEquals(1, hhA1.userSet.size, "userSet.5a")
		assertFalse(hhA1.hasUser(user1.id!!), "userSet.5b")
		assertTrue(hhA1.hasUser(user2.id!!), "userSet.5c")

		hhA1.clearUserSet()
		assertEquals(0, hhA1.userSet.size, "userSet.6a")
		assertFalse(hhA1.hasUser(user1.id!!), "userSet.6b")
		assertFalse(hhA1.hasUser(user2.id!!), "userSet.6c")

		hhA1.addUser(user2.id!!)
		assertEquals(1, hhA1.userSet.size, "userSet.7a")
		assertFalse(hhA1.hasUser(user1.id!!), "userSet.7b")
		assertTrue(hhA1.hasUser(user2.id!!), "userSet.7c")

		// Members
		val p1 = hhA1.addMember()
		p1.salutation = CodeSalutation.MR
		p1.name = "Martin"
		assertEquals(CodeSalutation.MR, p1.salutation, "p1.salutation")
		assertEquals("Martin", p1.name, "p1.name")
		val p1Id = p1.id

		val p2 = hhA1.addMember()
		p2.salutation = CodeSalutation.MRS
		p2.name = "Elena"
		assertEquals(CodeSalutation.MRS, p2.salutation, "p2.salutation")
		assertEquals("Elena", p2.name, "p2.name")
		val p2Id = p2.id

		p1.name = null
		assertNull(p1.name, "p1.name")
		p1.name = "Martin"
		assertEquals("Martin", p1.name, "p1.name")

		p2.salutation = null
		assertNull(p2.salutation, "p2.salutation")
		p2.salutation = CodeSalutation.MRS
		assertEquals(CodeSalutation.MRS, p2.salutation, "p2.salutation")

		assertEquals(2, hhA1.memberList.size, "members")
		assertEquals(p1, hhA1.getMember(0), "p1 by seqNr")
		assertEquals(p2, hhA1.getMember(1), "p2 by seqNr")
		assertEquals(p1, hhA1.getMemberById(p1Id), "p1 by id")
		assertEquals(p2, hhA1.getMemberById(p2Id), "p2 by id")

		p1.spouse = p2
		p2.spouseId = p1.id
		assertEquals(p2, p1.spouse, "p1.spouse")
		assertEquals(p1, p2.spouse, "p2.spouse")

		hhA1.mainMemberId = p1.id
		assertEquals(p1, hhA1.mainMember, "hh.mainMember")

		hhRepo.store(hhA1, user.id!!, OffsetDateTime.now())

		val hhA2 = hhRepo.get(hhA1Id!!) // Assuming hhA1Id is non-null
		assertNotNull(hhA2) // Ensure hhA2 is not null before further assertions
		assertNotSame(hhA1, hhA2, "different objs after load")
		assertEquals("HHA", hhA2.name, "name")

		assertEquals(1, hhA2.labelSet.size, "labelSet.a")
		assertFalse(hhA2.hasLabel(CodeLabel.A), "labelSet.b")
		assertTrue(hhA2.hasLabel(CodeLabel.B), "labelSet.c")

		assertEquals(1, hhA2.userSet.size, "userSet.a")
		assertFalse(hhA2.hasUser(user1.id!!), "userSet.b")
		assertTrue(hhA2.hasUser(user2.id!!), "userSet.c")

		assertEquals(2, hhA2.memberList.size, "members")

		val p12 = hhA2.getMember(0)
		assertNotNull(p12)
		assertEquals(CodeSalutation.MR, p12!!.salutation, "p1.salutation") // Smart cast after assertNotNull
		assertEquals("Martin", p12.name, "p1.name")

		val p22 = hhA2.getMember(1)
		assertNotNull(p22)
		assertEquals(CodeSalutation.MRS, p22!!.salutation, "p2.salutation")
		assertEquals("Elena", p22.name, "p2.name")

		assertEquals(p12, hhA2.getMember(0), "p1 by seqNr")
		assertEquals(p22, hhA2.getMember(1), "p2 by seqNr")
		assertEquals(p12, hhA2.getMemberById(p1Id), "p1 by id")
		assertEquals(p22, hhA2.getMemberById(p2Id), "p2 by id")

		assertEquals(p22, p12.spouse, "p1.spouse")
		assertEquals(p12, p22.spouse, "p2.spouse")
		assertEquals(p12, hhA2.mainMember, "hh.mainMember")

		assertEquals(1, hhRepo.getByForeignKey("objTypeId", "objHousehold").size, "1 hh")
	}
}
