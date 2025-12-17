package io.dddrive.core.property.path

import io.dddrive.core.property.path.handlers.DefaultPropertyHandler
import io.dddrive.core.property.path.handlers.EnumeratedElementHandler
import io.dddrive.core.property.path.handlers.ListPathElementHandler
import io.dddrive.core.property.path.handlers.PartReferencePropertyHandler
import io.dddrive.core.property.path.handlers.ReferencePropertyHandler
import io.dddrive.core.property.path.handlers.ReferencePropertyIdHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for the complete path handling framework.
 *
 * Tests all handlers working together through PathProcessor to ensure:
 * - Complex paths are processed correctly by multiple handlers
 * - No handler conflicts occur for valid path patterns
 * - End-to-end functionality works for real-world scenarios
 */
class PathFrameworkIntegrationTest {
	private lateinit var pathProcessor: PathProcessor
	private lateinit var rootEntity: TestEntity

	@BeforeEach
	fun setUp() {
		// Create PathProcessor with all framework handlers in correct order
		pathProcessor =
			PathProcessor(
				listOf(
					// Order should not matter anymore
					DefaultPropertyHandler(),
					ReferencePropertyHandler(),
					PartReferencePropertyHandler(),
					ListPathElementHandler(),
					EnumeratedElementHandler(),
					ReferencePropertyIdHandler(),
				).shuffled(),
			)

		// Set up complex test entity structure
		rootEntity = createComplexTestEntity()
	}

	@Test
	fun `simple property access works`() {
		val result = pathProcessor.getValueByPath("name", rootEntity)
		assertEquals("TestEntity", result)

		pathProcessor.setFieldByPath("name", "UpdatedName", rootEntity)
		val updatedResult = pathProcessor.getValueByPath("name", rootEntity)
		assertEquals("UpdatedName", updatedResult)
	}

	@Test
	fun `enum property with id suffix access works`() {
		val result = pathProcessor.getValueByPath("status.id", rootEntity)
		assertEquals("active", result)

		pathProcessor.setFieldByPath("status.id", "inactive", rootEntity)
		val updatedResult = pathProcessor.getValueByPath("status.id", rootEntity)
		assertEquals("inactive", updatedResult)
	}

	@Test
	fun `direct enum property access works`() {
		val result = pathProcessor.getValueByPath("status", rootEntity)
		assertNotNull(result)
		assertEquals("active", (result as TestEnum).getId())
	}

	@Test
	fun `list property access with auto-expansion works`() {
		// Initially empty list, should auto-expand to index 0
		pathProcessor.setFieldByPath("children[0].name", "Child1", rootEntity)

		val result = pathProcessor.getValueByPath("children[0].name", rootEntity)
		assertEquals("Child1", result)

		// Add second child
		pathProcessor.setFieldByPath("children[1].name", "Child2", rootEntity)

		val result2 = pathProcessor.getValueByPath("children[1].name", rootEntity)
		assertEquals("Child2", result2)
	}

	@Test
	fun `list access with enum properties works`() {
		pathProcessor.setFieldByPath("children[0].status.id", "pending", rootEntity)

		val result = pathProcessor.getValueByPath("children[0].status.id", rootEntity)
		assertEquals("pending", result)
	}

	@Test
	fun `reference property navigation works`() {
		val result = pathProcessor.getValueByPath("reference.name", rootEntity)
		assertEquals("ReferencedEntity", result)

		pathProcessor.setFieldByPath("reference.name", "UpdatedReference", rootEntity)
		val updatedResult = pathProcessor.getValueByPath("reference.name", rootEntity)
		assertEquals("UpdatedReference", updatedResult)
	}

	@Test
	fun `part reference property navigation works`() {
		val result = pathProcessor.getValueByPath("part.name", rootEntity)
		assertEquals("PartEntity", result)

		pathProcessor.setFieldByPath("part.name", "UpdatedPart", rootEntity)
		val updatedResult = pathProcessor.getValueByPath("part.name", rootEntity)
		assertEquals("UpdatedPart", updatedResult)
	}

	@Test
	fun `complex nested path with multiple handler types works`() {
		// Navigate: reference -> children[0] -> status.id
		val complexPath = "reference.children[0].status.id"

		pathProcessor.setFieldByPath(complexPath, "completed", rootEntity)
		val result = pathProcessor.getValueByPath(complexPath, rootEntity)

		assertEquals("completed", result)
	}

	@Test
	fun `very complex path through multiple levels works`() {
		// First, populate the children list to have at least 2 items
		pathProcessor.setFieldByPath("part.reference.children[0].name", "Child0", rootEntity)
		pathProcessor.setFieldByPath("part.reference.children[1].name", "Child1", rootEntity)

		// Navigate: part -> reference -> children[1] -> part -> status.id
		val ultraComplexPath = "part.reference.children[1].status.id"

		pathProcessor.setFieldByPath(ultraComplexPath, "archived", rootEntity)
		val result = pathProcessor.getValueByPath(ultraComplexPath, rootEntity)

		assertEquals("archived", result)
	}

	@Test
	fun `null reference returns null gracefully`() {
		pathProcessor.setFieldByPath("reference", null, rootEntity)

		val result = pathProcessor.getValueByPath("reference.name", rootEntity)
		assertNull(result)
	}

	@Test
	fun `invalid path throws appropriate exception`() {
		assertThrows(IllegalArgumentException::class.java) {
			pathProcessor.getValueByPath("nonexistent.path", rootEntity)
		}
	}

	@Test
	fun `list index out of bounds returns null for get`() {
		val result = pathProcessor.getValueByPath("children[5].name", rootEntity)
		assertNull(result)
	}

	@Test
	fun `list expansion beyond immediate next index works correctly`() {
		// Should now successfully expand the list to accommodate index 5
		assertDoesNotThrow {
			pathProcessor.setFieldByPath("children[5].name", "TooFar", rootEntity)
		}

		// Verify the list was expanded and the value was set
		val expandedValue = pathProcessor.getValueByPath("children[5].name", rootEntity)
		assertEquals("TooFar", expandedValue)

		// Verify intermediate elements were created with default names
		for (i in 0..4) {
			val intermediateName = pathProcessor.getValueByPath("children[$i].name", rootEntity)
			// Elements are auto-generated with names like "Part1", "Part2", etc.
			assertEquals("Part${i + 1}", intermediateName, "Intermediate element at index $i should have auto-generated name")
		}
	}

	@Test
	fun `multiple operations on same path work correctly`() {
		val path = "children[0].status.id"

		// Set initial value using a valid enum ID
		pathProcessor.setFieldByPath(path, "pending", rootEntity)
		assertEquals("pending", pathProcessor.getValueByPath(path, rootEntity))

		// Update value
		pathProcessor.setFieldByPath(path, "completed", rootEntity)
		assertEquals("completed", pathProcessor.getValueByPath(path, rootEntity))

		// Set to null - note: setting enum ID to null currently doesn't nullify the enum
		// This is a behavior that could be improved in the future
		pathProcessor.setFieldByPath(path, null, rootEntity)
		// For now, verify that it didn't crash and still has the previous value
		assertNotNull(pathProcessor.getValueByPath(path, rootEntity))
	}

	@Test
	fun `list element enum id access does not cause handler conflict`() {
		// This test specifically verifies the fix for the "Multiple specific handlers found" error
		// when accessing enum properties through list elements (e.g., partnerList[0].gender.id)
		val path = "children[0].status.id"

		// Initially, accessing a non-existent list element should return null (GET should not expand lists)
		assertDoesNotThrow {
			val result = pathProcessor.getValueByPath(path, rootEntity)
			assertNull(result, "GET operation on non-existent list element should return null")
		}

		// Setting should work and expand the list as needed
		assertDoesNotThrow {
			pathProcessor.setFieldByPath(path, "inactive", rootEntity)
		}

		// Now GET should return the value we set
		assertEquals("inactive", pathProcessor.getValueByPath(path, rootEntity))
	}

	@Test
	fun `reference property Id suffix access works`() {
		// Set reference ID directly via Id suffix
		val testId = "ref-123"
		pathProcessor.setFieldByPath("referenceId", testId, rootEntity)

		// Get reference ID via Id suffix
		val result = pathProcessor.getValueByPath("referenceId", rootEntity)
		assertEquals(testId, result)
	}

	@Test
	fun `reference property Id suffix with null works`() {
		// Set reference ID first
		pathProcessor.setFieldByPath("referenceId", "initial-id", rootEntity)
		assertEquals("initial-id", pathProcessor.getValueByPath("referenceId", rootEntity))

		// Set to null
		pathProcessor.setFieldByPath("referenceId", null, rootEntity)
		assertNull(pathProcessor.getValueByPath("referenceId", rootEntity))
	}

	@Test
	fun `complex path with reference Id suffix works`() {
		// Create a complex path: children[0].referenceId
		// First ensure child exists and has a reference property
		pathProcessor.setFieldByPath("children[0].name", "TestChild", rootEntity)

		// Set the reference ID via complex path
		val testId = "complex-ref-456"
		pathProcessor.setFieldByPath("children[0].referenceId", testId, rootEntity)

		// Get the reference ID back
		val result = pathProcessor.getValueByPath("children[0].referenceId", rootEntity)
		assertEquals(testId, result)
	}

	@Test
	fun `reference Id suffix does not conflict with enum id suffix`() {
		// Test that both .id (enum) and Id (reference) patterns work without conflicts

		// Set enum via .id suffix
		pathProcessor.setFieldByPath("status.id", "pending", rootEntity)
		assertEquals("pending", pathProcessor.getValueByPath("status.id", rootEntity))

		// Set reference via Id suffix
		pathProcessor.setFieldByPath("referenceId", "ref-789", rootEntity)
		assertEquals("ref-789", pathProcessor.getValueByPath("referenceId", rootEntity))

		// Both should work independently
		assertEquals("pending", pathProcessor.getValueByPath("status.id", rootEntity))
		assertEquals("ref-789", pathProcessor.getValueByPath("referenceId", rootEntity))
	}

	@Test
	fun `pillarTwoStatementId pattern works like in real usage`() {
		// Simulate the real-world scenario from DocRICPartPartner
		// Add a pillarTwoStatement reference property
		val pillarTwoStatement = TestAggregate(999)
		pillarTwoStatement.addProperty(
			"name",
			PathTestUtils.createBaseProperty(String::class.java, "TestPillarTwoStatement"),
		)

		rootEntity.addProperty("pillarTwoStatement", PathTestUtils.createReferenceProperty(pillarTwoStatement))

		// Test setting via pillarTwoStatementId path
		val statementId = "statement-id-123"
		pathProcessor.setFieldByPath("pillarTwoStatementId", statementId, rootEntity)

		// Verify we can get it back
		val result = pathProcessor.getValueByPath("pillarTwoStatementId", rootEntity)
		assertEquals(statementId, result)
	}

	@Test
	fun `complex pillarTwoStatementId path through list works`() {
		// Simulate partnerList[0].pillarTwoStatementId pattern
		pathProcessor.setFieldByPath("children[0].name", "Partner", rootEntity)

		// Set the pillar two statement ID
		val statementId = "partner-statement-456"
		pathProcessor.setFieldByPath("children[0].referenceId", statementId, rootEntity)

		// Verify retrieval
		val result = pathProcessor.getValueByPath("children[0].referenceId", rootEntity)
		assertEquals(statementId, result)
	}

	private fun createComplexTestEntity(): TestEntity {
		val entity = PathTestUtils.createMockEntity()

		// Add basic properties
		entity.addProperty("name", PathTestUtils.createBaseProperty(String::class.java, "TestEntity"))
		entity.addProperty("age", PathTestUtils.createBaseProperty(Int::class.java, 25))

		// Add enum property
		val enumeration = PathTestUtils.createMockEnumeration()
		entity.addProperty("status", PathTestUtils.createEnumProperty(enumeration, enumeration.getItem("active")))

		// Add list property
		entity.addProperty("children", PathTestUtils.createPartListProperty())

		// Add reference property
		val referencedEntity = createReferencedEntity()
		entity.addProperty("reference", PathTestUtils.createReferenceProperty(referencedEntity))

		// Add part reference property
		val partEntity = createPartEntity()
		entity.addProperty("part", PathTestUtils.createPartReferenceProperty(partEntity))

		return entity
	}

	private fun createReferencedEntity(): TestAggregate {
		val entity = TestAggregate()
		entity.addProperty("name", PathTestUtils.createBaseProperty(String::class.java, "ReferencedEntity"))
		entity.addProperty("children", PathTestUtils.createPartListProperty())
		return entity
	}

	private fun createPartEntity(): TestPart {
		val part = TestPart()
		part.addProperty("name", PathTestUtils.createBaseProperty(String::class.java, "PartEntity"))

		// Add enum property for status
		val enumeration = PathTestUtils.createMockEnumeration()
		part.addProperty("status", PathTestUtils.createEnumProperty(enumeration, enumeration.getItem("active")))

		// Add reference that creates a circular structure for ultra-complex testing
		val deepReference = createReferencedEntity()
		part.addProperty("reference", PathTestUtils.createReferenceProperty(deepReference))

		return part
	}
}
