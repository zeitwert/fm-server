package io.zeitwert.fm.oe

import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import io.zeitwert.test.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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
	private lateinit var userRepository: ObjUserRepository

	@Autowired
	private lateinit var tenantRepository: ObjTenantRepository

	private lateinit var t1: ObjTenant
	private lateinit var t2: ObjTenant
	private lateinit var t3: ObjTenant

	@BeforeEach
	fun setUp() {
		t1 = tenantRepository.getByKey("tt1").orElseGet {
			tenantRepository.create().apply {
				key = "tt1"
				name = "tt1"
				tenantType = CodeTenantType.COMMUNITY
				assertNotNull(id, "tenant t1.id not null")
				assertNotNull(tenantId, "tenant t1.tenantId not null")
				tenantRepository.store(this)
			}
		}
		t2 = tenantRepository.getByKey("tt2").orElseGet {
			tenantRepository.create().apply {
				key = "tt2"
				name = "tt2"
				tenantType = CodeTenantType.COMMUNITY
				tenantRepository.store(this)
			}
		}
		t3 = tenantRepository.getByKey("tt3").orElseGet {
			tenantRepository.create().apply {
				key = "tt3"
				name = "tt3"
				tenantType = CodeTenantType.COMMUNITY
				tenantRepository.store(this)
			}
		}
	}

	@Test
	fun testUserBase() {
		assertNotNull(userRepository, "userRepository not null")
		assertEquals("obj_user", userRepository.aggregateType.id)

		val user1 = userRepository.create()
		assertNotNull(user1, "user not null")
		requireNotNull(user1)

		assertNotNull(user1.id, "id not null")
		assertNotNull(user1.tenantId, "tenant not null")
		initUser(user1, uniqueEmail("base-test"), "Base Test User", CodeUserRole.USER)

		val user1Id = user1.id
		val user1IdHash = System.identityHashCode(user1)

		assertFalse(user1.meta.isFrozen, "not frozen")
		assertNotNull(user1.meta.createdByUserId, "createdByUser not null")
		assertNotNull(user1.meta.createdAt, "createdAt not null")
		assertEquals(1, user1.meta.transitionList.size)

		userRepository.store(user1)

		val user2 = userRepository.get(user1Id)
		val user2IdHash = System.identityHashCode(user2)
		assertNotEquals(user1IdHash, user2IdHash)

		assertTrue(user2.meta.isFrozen, "frozen")
		assertNotNull(user2.meta.modifiedByUserId, "modifiedByUser not null")
		assertNotNull(user2.meta.modifiedAt, "modifiedAt not null")
	}

	@Test
	fun testUserProperties() {
		val user1 = userRepository.create()
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

		userRepository.store(user1)

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

		userRepository.store(user2)

		val user3 = userRepository.load(user1Id)

		assertEquals(email2, user3.email)
		assertEquals("Jane Doe", user3.name)
		assertEquals("Updated description", user3.description)
		assertEquals(CodeUserRole.SUPER_USER, user3.role)
	}

	@Test
	fun testUserTenantSet() {
		val user1 = userRepository.create()
		requireNotNull(user1)

		val user1Id = user1.id
		initUser(user1, uniqueEmail("multi-tenant"), "Multi Tenant User", CodeUserRole.ADMIN)

		// Initially no additional tenants
		assertEquals(0, user1.tenantSet.size)

		// Add tenants
		user1.tenantSet.add(t1.id)
		assertEquals(1, user1.tenantSet.size)
		assertTrue(user1.tenantSet.any { it == t1.id })

		user1.tenantSet.add(t2.id)
		assertEquals(2, user1.tenantSet.size)
		assertTrue(user1.tenantSet.any { it == t2.id })

		user1.tenantSet.add(t3.id)
		assertEquals(3, user1.tenantSet.size)

		// Remove one tenant
		user1.tenantSet.remove(t2.id)
		assertEquals(2, user1.tenantSet.size)
		assertFalse(user1.tenantSet.any { it == t2.id })

		assertEquals(1, user1.meta.transitionList.size)
		userRepository.store(user1)
		assertEquals(2, user1.meta.transitionList.size)

		// Load and verify persistence
		val user2 = userRepository.load(user1Id)
		assertEquals(2, user2.meta.transitionList.size)

		assertEquals(2, user2.tenantSet.size)
		assertTrue(user2.tenantSet.any { it == t1.id })
		assertFalse(user2.tenantSet.any { it == t2.id })
		assertTrue(user2.tenantSet.any { it == t3.id })

		// Modify the set
		user2.tenantSet.remove(t1.id)
		user2.tenantSet.add(t2.id)

		assertEquals(2, user2.tenantSet.size)
		assertFalse(user2.tenantSet.any { it == t1.id })
		assertTrue(user2.tenantSet.any { it == t2.id })
		assertTrue(user2.tenantSet.any { it == t3.id })

		userRepository.store(user2)

		// Verify final state
		val user3 = userRepository.load(user1Id)
		assertEquals(2, user3.tenantSet.size)
		assertFalse(user3.tenantSet.any { it == t1.id })
		assertTrue(user3.tenantSet.any { it == t2.id })
		assertTrue(user3.tenantSet.any { it == t3.id })
	}

	@Test
	fun testUserClearTenantSet() {
		val user1 = userRepository.create()
		requireNotNull(user1)

		val user1Id = user1.id
		initUser(user1, uniqueEmail("clear-test"), "Clear Test User", CodeUserRole.USER)

		user1.tenantSet.add(t1.id)
		user1.tenantSet.add(t2.id)
		assertEquals(2, user1.tenantSet.size)

		userRepository.store(user1)

		// Load and clear
		val user2 = userRepository.load(user1Id)
		assertEquals(2, user2.tenantSet.size)

		user2.tenantSet.clear()
		assertEquals(0, user2.tenantSet.size)

		userRepository.store(user2)

		// Verify cleared
		val user3 = userRepository.load(user1Id)
		assertEquals(0, user3.tenantSet.size)
	}

	@Test
	fun testUserTransitionList() {
		val user1 = userRepository.create()
		requireNotNull(user1)

		val user1Id = user1.id
		initUser(user1, uniqueEmail("transition-test"), "Transition Test User", CodeUserRole.USER)

		// First transition is from creation
		assertEquals(1, user1.meta.transitionList.size)

		userRepository.store(user1)

		// Second transition after first store
		assertEquals(2, user1.meta.transitionList.size)

		val user2 = userRepository.load(user1Id)
		assertEquals(2, user2.meta.transitionList.size)

		user2.name = "Updated Name"
		userRepository.store(user2)

		// Third transition after update
		assertEquals(3, user2.meta.transitionList.size)

		val user3 = userRepository.load(user1Id)
		assertEquals(3, user3.meta.transitionList.size)
		assertEquals("Updated Name", user3.name)
	}

	private fun uniqueEmail(prefix: String): String {
		val uuid = UUID.randomUUID().toString().substring(0, 8)
		return "$prefix-$uuid@example.com"
	}

	private fun initUser(
		user: ObjUser,
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
