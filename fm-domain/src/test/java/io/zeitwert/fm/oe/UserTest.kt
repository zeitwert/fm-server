package io.zeitwert.fm.oe

import io.dddrive.oe.model.ObjTenant
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
class UserTest {

	@Autowired
	private lateinit var requestCtx: RequestContextFM

	@Autowired
	private lateinit var userRepository: ObjUserFMRepository

	@Autowired
	private lateinit var tenantRepository: ObjTenantFMRepository

	@Test
	fun testUserBase() {
		assertNotNull(userRepository, "userRepository not null")
		assertEquals("obj_user", userRepository.aggregateType.id)

		val tenantId = requestCtx.tenantId
		val userId = requestCtx.userId
		val now = requestCtx.currentTime

		val user1 = userRepository.create(tenantId, userId, now)
		assertNotNull(user1, "user not null")
		requireNotNull(user1)

		assertNotNull(user1.id, "id not null")
		assertNotNull(user1.tenantId, "tenant not null")
		initUser(user1, uniqueEmail("base-test"), "Base Test User", CodeUserRole.USER)

		val user1Id = user1.id
		val user1IdHash = System.identityHashCode(user1)

		assertFalse(user1.meta.isFrozen, "not frozen")
		assertNotNull(user1.meta.createdByUser, "createdByUser not null")
		assertNotNull(user1.meta.createdAt, "createdAt not null")
		assertEquals(1, user1.meta.transitionList.size)

		userRepository.store(user1, userId, now)

		val user2 = userRepository.get(user1Id)
		val user2IdHash = System.identityHashCode(user2)
		assertNotEquals(user1IdHash, user2IdHash)

		assertTrue(user2.meta.isFrozen, "frozen")
		assertNotNull(user2.meta.modifiedByUser, "modifiedByUser not null")
		assertNotNull(user2.meta.modifiedAt, "modifiedAt not null")
	}

	@Test
	fun testUserProperties() {
		val tenantId = requestCtx.tenantId
		val userId = requestCtx.userId
		val now = requestCtx.currentTime

		val user1 = userRepository.create(tenantId, userId, now)
		requireNotNull(user1)

		val user1Id = user1.id
		val email1 = uniqueEmail("john-props")
		initUser(user1, email1, "John Doe", CodeUserRole.ADMIN)

		assertEquals(email1, user1.email)
		assertEquals("John Doe", user1.name)
		assertEquals("Test description", user1.description)
		assertEquals(CodeUserRole.ADMIN, user1.role)
		assertTrue(user1.hasRole(CodeUserRole.ADMIN))
		assertFalse(user1.hasRole(CodeUserRole.USER))

		userRepository.store(user1, userId, now)

		val user2 = userRepository.load(user1Id)

		assertEquals(email1, user2.email)
		assertEquals("John Doe", user2.name)
		assertEquals("Test description", user2.description)
		assertEquals(CodeUserRole.ADMIN, user2.role)

		// Update properties
		val email2 = uniqueEmail("jane-props")
		user2.email = email2
		user2.name = "Jane Doe"
		user2.description = "Updated description"
		user2.role = CodeUserRole.SUPER_USER

		assertEquals(email2, user2.email)
		assertEquals("Jane Doe", user2.name)
		assertEquals("Updated description", user2.description)
		assertEquals(CodeUserRole.SUPER_USER, user2.role)

		userRepository.store(user2, userId, now)

		val user3 = userRepository.load(user1Id)

		assertEquals(email2, user3.email)
		assertEquals("Jane Doe", user3.name)
		assertEquals("Updated description", user3.description)
		assertEquals(CodeUserRole.SUPER_USER, user3.role)
	}

	@Test
	fun testUserTenantSet() {
		val tenantId = requestCtx.tenantId
		val userId = requestCtx.userId
		val now = requestCtx.currentTime

		// Get existing tenants from the system
		val existingTenants = getExistingTenants()

		// Skip test if not enough tenants exist
		if (existingTenants.size < 3) {
			println("Skipping testUserTenantSet: requires at least 3 existing tenants, found ${existingTenants.size}")
			return
		}

		val tenantA = existingTenants[0]
		val tenantB = existingTenants[1]
		val tenantC = existingTenants[2]

		// Create a user
		val user1 = userRepository.create(tenantId, userId, now)
		requireNotNull(user1)

		val user1Id = user1.id
		initUser(user1, uniqueEmail("multi-tenant"), "Multi Tenant User", CodeUserRole.ADMIN)

		// Initially no additional tenants
		assertEquals(0, user1.tenantSet.size)

		// Add tenants
		user1.tenantSet.add(tenantA.id)
		assertEquals(1, user1.tenantSet.size)
		assertTrue(user1.tenantSet.any { it == tenantA.id })

		user1.tenantSet.add(tenantB.id)
		assertEquals(2, user1.tenantSet.size)
		assertTrue(user1.tenantSet.any { it == tenantB.id })

		user1.tenantSet.add(tenantC.id)
		assertEquals(3, user1.tenantSet.size)

		// Remove one tenant
		user1.tenantSet.remove(tenantB.id)
		assertEquals(2, user1.tenantSet.size)
		assertFalse(user1.tenantSet.any { it == tenantB.id })

		assertEquals(1, user1.meta.transitionList.size)
		userRepository.store(user1, userId, now)
		assertEquals(2, user1.meta.transitionList.size)

		// Load and verify persistence
		val user2 = userRepository.load(user1Id)
		assertEquals(2, user2.meta.transitionList.size)

		assertEquals(2, user2.tenantSet.size)
		assertTrue(user2.tenantSet.any { it == tenantA.id })
		assertFalse(user2.tenantSet.any { it == tenantB.id })
		assertTrue(user2.tenantSet.any { it == tenantC.id })

		// Modify the set
		user2.tenantSet.remove(tenantA.id)
		user2.tenantSet.add(tenantB.id)

		assertEquals(2, user2.tenantSet.size)
		assertFalse(user2.tenantSet.any { it == tenantA.id })
		assertTrue(user2.tenantSet.any { it == tenantB.id })
		assertTrue(user2.tenantSet.any { it == tenantC.id })

		userRepository.store(user2, userId, now)

		// Verify final state
		val user3 = userRepository.load(user1Id)
		assertEquals(2, user3.tenantSet.size)
		assertFalse(user3.tenantSet.any { it == tenantA.id })
		assertTrue(user3.tenantSet.any { it == tenantB.id })
		assertTrue(user3.tenantSet.any { it == tenantC.id })
	}

	@Test
	fun testUserClearTenantSet() {
		val tenantId = requestCtx.tenantId
		val userId = requestCtx.userId
		val now = requestCtx.currentTime

		// Get existing tenants from the system
		val existingTenants = getExistingTenants()

		// Skip test if not enough tenants exist
		if (existingTenants.size < 2) {
			println("Skipping testUserClearTenantSet: requires at least 2 existing tenants, found ${existingTenants.size}")
			return
		}

		val tenantA = existingTenants[0]
		val tenantB = existingTenants[1]

		// Create user with tenants
		val user1 = userRepository.create(tenantId, userId, now)
		requireNotNull(user1)

		val user1Id = user1.id
		initUser(user1, uniqueEmail("clear-test"), "Clear Test User", CodeUserRole.USER)

		user1.tenantSet.add(tenantA.id)
		user1.tenantSet.add(tenantB.id)
		assertEquals(2, user1.tenantSet.size)

		userRepository.store(user1, userId, now)

		// Load and clear
		val user2 = userRepository.load(user1Id)
		assertEquals(2, user2.tenantSet.size)

		user2.tenantSet.clear()
		assertEquals(0, user2.tenantSet.size)

		userRepository.store(user2, userId, now)

		// Verify cleared
		val user3 = userRepository.load(user1Id)
		assertEquals(0, user3.tenantSet.size)
	}

	@Test
	fun testUserTransitionList() {
		val tenantId = requestCtx.tenantId
		val userId = requestCtx.userId
		val now = requestCtx.currentTime

		val user1 = userRepository.create(tenantId, userId, now)
		requireNotNull(user1)

		val user1Id = user1.id
		initUser(user1, uniqueEmail("transition-test"), "Transition Test User", CodeUserRole.USER)

		// First transition is from creation
		assertEquals(1, user1.meta.transitionList.size)

		userRepository.store(user1, userId, now)

		// Second transition after first store
		assertEquals(2, user1.meta.transitionList.size)

		val user2 = userRepository.load(user1Id)
		assertEquals(2, user2.meta.transitionList.size)

		user2.name = "Updated Name"
		userRepository.store(user2, userId, now)

		// Third transition after update
		assertEquals(3, user2.meta.transitionList.size)

		val user3 = userRepository.load(user1Id)
		assertEquals(3, user3.meta.transitionList.size)
		assertEquals("Updated Name", user3.name)
	}

	private fun getExistingTenants(): List<ObjTenant> {
		requestCtx.tenantId
		val tenantIds = tenantRepository.find(null)
		return tenantIds.mapNotNull { id ->
			try {
				tenantRepository.get(id)
			} catch (e: Exception) {
				null
			}
		}
	}

	private fun uniqueEmail(prefix: String): String {
		val uuid = UUID.randomUUID().toString().substring(0, 8)
		return "$prefix-$uuid@example.com"
	}

	private fun initUser(
		user: ObjUserFM,
		email: String,
		name: String,
		role: CodeUserRole,
	) {
		user.email = email
		user.name = name
		user.description = "Test description"
		user.role = role
		// Use a pre-encoded BCrypt password for "test-password"
		user.password = "\$2a\$10\$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"
	}

}
