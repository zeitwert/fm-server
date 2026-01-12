package dddrive.test

import dddrive.db.MemoryDb
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjUser
import dddrive.query.query
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for [MemoryDb] singleton.
 *
 * Tests store, get, and find operations using Household, Tenant, and User
 * as type discriminators.
 */
class MemoryDbTest {

	@BeforeEach
	fun setUp() {
		MemoryDb.clear()
	}

	// --- Store and Get Tests ---

	@Test
	fun testStoreAndGetTenant() {
		val tenant = mapOf("id" to 1, "name" to "Tenant A", "key" to "TENANT_A")
		MemoryDb.store(ObjTenant::class.java, tenant)

		val result = MemoryDb.get(ObjTenant::class.java, 1)
		assertNotNull(result)
		assertEquals("Tenant A", result?.get("name"))
		assertEquals("TENANT_A", result?.get("key"))
	}

	@Test
	fun testStoreAndGetUser() {
		val user = mapOf("id" to 100, "email" to "user@example.com", "name" to "John Doe")
		MemoryDb.store(ObjUser::class.java, user)

		val result = MemoryDb.get(ObjUser::class.java, 100)
		assertNotNull(result)
		assertEquals("user@example.com", result?.get("email"))
		assertEquals("John Doe", result?.get("name"))
	}

	@Test
	fun testStoreAndGetHousehold() {
		val household = mapOf("id" to 42, "name" to "Smith Family", "tenantId" to 1)
		MemoryDb.store(ObjHousehold::class.java, household)

		val result = MemoryDb.get(ObjHousehold::class.java, 42)
		assertNotNull(result)
		assertEquals("Smith Family", result?.get("name"))
		assertEquals(1, result?.get("tenantId"))
	}

	@Test
	fun testGetNonExistent() {
		val result = MemoryDb.get(ObjTenant::class.java, 999)
		assertNull(result)
	}

	@Test
	fun testGetWrongType() {
		val tenant = mapOf("id" to 1, "name" to "Tenant A")
		MemoryDb.store(ObjTenant::class.java, tenant)

		// Same ID but different type should return null
		val result = MemoryDb.get(ObjUser::class.java, 1)
		assertNull(result)
	}

	@Test
	fun testStoreOverwritesExisting() {
		val tenant1 = mapOf("id" to 1, "name" to "Original")
		MemoryDb.store(ObjTenant::class.java, tenant1)

		val tenant2 = mapOf("id" to 1, "name" to "Updated")
		MemoryDb.store(ObjTenant::class.java, tenant2)

		val result = MemoryDb.get(ObjTenant::class.java, 1)
		assertEquals("Updated", result?.get("name"))
	}

	// --- Find All Tests ---

	@Test
	fun testFindAllOfType() {
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 1, "name" to "User 1"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 2, "name" to "User 2"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 3, "name" to "User 3"))

		val results = MemoryDb.find(ObjUser::class.java, null)
		assertEquals(3, results.size)
	}

	@Test
	fun testFindAllWithEmptyQuery() {
		MemoryDb.store(ObjTenant::class.java, mapOf("id" to 1, "name" to "Tenant 1"))
		MemoryDb.store(ObjTenant::class.java, mapOf("id" to 2, "name" to "Tenant 2"))

		val results = MemoryDb.find(ObjTenant::class.java, query { })
		assertEquals(2, results.size)
	}

	@Test
	fun testFindAllAcrossTypes() {
		MemoryDb.store(ObjTenant::class.java, mapOf("id" to 1, "name" to "Tenant"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 2, "name" to "User"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 3, "name" to "Household"))

		// null type searches across all types
		val results = MemoryDb.find(null, null)
		assertEquals(3, results.size)
	}

	// --- Find with EQ Filter Tests ---

	@Test
	fun testFindWithEqFilter() {
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 1, "name" to "A", "tenantId" to 10))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 2, "name" to "B", "tenantId" to 20))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 3, "name" to "C", "tenantId" to 10))

		val results =
			MemoryDb.find(
				ObjHousehold::class.java,
				query {
					filter { "tenantId" eq 10 }
				},
			)

		assertEquals(2, results.size)
		assertTrue(results.any { it["id"] == 1 })
		assertTrue(results.any { it["id"] == 3 })
	}

	@Test
	fun testFindWithEqFilterIdSuffix() {
		// Test the "Id" suffix fallback logic
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 1, "responsibleUserId" to 100))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 2, "responsibleUserId" to 200))

		// Query with "responsibleUser" should match "responsibleUserId"
		val results =
			MemoryDb.find(
				ObjHousehold::class.java,
				query {
					filter { "responsibleUser" eq 100 }
				},
			)

		assertEquals(1, results.size)
		assertEquals(1, results[0]["id"])
	}

	@Test
	fun testFindWithEqFilterNoMatch() {
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 1, "name" to "John"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 2, "name" to "Jane"))

		val results =
			MemoryDb.find(
				ObjUser::class.java,
				query {
					filter { "name" eq "Bob" }
				},
			)

		assertEquals(0, results.size)
	}

	// --- Find with IN Filter Tests ---

	@Test
	fun testFindWithInFilter() {
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 1, "role" to "admin"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 2, "role" to "user"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 3, "role" to "guest"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 4, "role" to "admin"))

		val results =
			MemoryDb.find(
				ObjUser::class.java,
				query {
					filter { "role" inList listOf("admin", "user") }
				},
			)

		assertEquals(3, results.size)
		assertTrue(results.any { it["id"] == 1 })
		assertTrue(results.any { it["id"] == 2 })
		assertTrue(results.any { it["id"] == 4 })
	}

	// --- Find with OR Filter Tests ---

	@Test
	fun testFindWithOrFilter() {
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 1, "name" to "A", "status" to "active"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 2, "name" to "B", "status" to "closed"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 3, "name" to "C", "status" to "pending"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 4, "name" to "D", "status" to "active"))

		val results =
			MemoryDb.find(
				ObjHousehold::class.java,
				query {
					or {
						filter { "status" eq "active" }
						filter { "status" eq "closed" }
					}
				},
			)

		assertEquals(3, results.size)
		assertTrue(results.any { it["id"] == 1 })
		assertTrue(results.any { it["id"] == 2 })
		assertTrue(results.any { it["id"] == 4 })
	}

	// --- Find with Multiple Filters (AND) Tests ---

	@Test
	fun testFindWithMultipleFilters() {
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 1, "tenantId" to 10, "status" to "active"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 2, "tenantId" to 10, "status" to "closed"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 3, "tenantId" to 20, "status" to "active"))

		val results =
			MemoryDb.find(
				ObjHousehold::class.java,
				query {
					filter { "tenantId" eq 10 }
					filter { "status" eq "active" }
				},
			)

		assertEquals(1, results.size)
		assertEquals(1, results[0]["id"])
	}

	// --- Clear Tests ---

	@Test
	fun testClearByType() {
		MemoryDb.store(ObjTenant::class.java, mapOf("id" to 1, "name" to "Tenant"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 1, "name" to "User"))

		MemoryDb.clear(ObjTenant::class.java)

		// Tenant should be cleared
		assertNull(MemoryDb.get(ObjTenant::class.java, 1))

		// User should still exist
		assertNotNull(MemoryDb.get(ObjUser::class.java, 1))
	}

	@Test
	fun testClearAll() {
		MemoryDb.store(ObjTenant::class.java, mapOf("id" to 1, "name" to "Tenant"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 1, "name" to "User"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 1, "name" to "Household"))

		MemoryDb.clear()

		assertNull(MemoryDb.get(ObjTenant::class.java, 1))
		assertNull(MemoryDb.get(ObjUser::class.java, 1))
		assertNull(MemoryDb.get(ObjHousehold::class.java, 1))
	}

	// --- Type Isolation Tests ---

	@Test
	fun testTypesAreIsolated() {
		// Store same ID under different types
		MemoryDb.store(ObjTenant::class.java, mapOf("id" to 1, "name" to "Tenant 1"))
		MemoryDb.store(ObjUser::class.java, mapOf("id" to 1, "name" to "User 1"))
		MemoryDb.store(ObjHousehold::class.java, mapOf("id" to 1, "name" to "Household 1"))

		// Each type should have its own object
		assertEquals("Tenant 1", MemoryDb.get(ObjTenant::class.java, 1)?.get("name"))
		assertEquals("User 1", MemoryDb.get(ObjUser::class.java, 1)?.get("name"))
		assertEquals("Household 1", MemoryDb.get(ObjHousehold::class.java, 1)?.get("name"))

		// Find should only return objects of the specified type
		assertEquals(1, MemoryDb.find(ObjTenant::class.java, null).size)
		assertEquals(1, MemoryDb.find(ObjUser::class.java, null).size)
		assertEquals(1, MemoryDb.find(ObjHousehold::class.java, null).size)
	}
}
