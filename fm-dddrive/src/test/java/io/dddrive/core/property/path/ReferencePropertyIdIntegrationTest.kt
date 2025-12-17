package io.dddrive.core.property.path

import io.dddrive.core.property.path.handlers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration test specifically for ReferencePropertyIdHandler in real-world scenarios.
 * Tests the actual use case from DocRIC: "partnerList[0].pillarTwoStatementId"
 */
class ReferencePropertyIdIntegrationTest {
	private lateinit var pathProcessor: PathProcessor
	private lateinit var rootEntity: TestEntity

	@BeforeEach
	fun setUp() {
		// Create PathProcessor with all framework handlers
		pathProcessor =
			PathProcessor(
				listOf(
					DefaultPropertyHandler(),
					ReferencePropertyHandler(),
					PartReferencePropertyHandler(),
					ListPathElementHandler(),
					EnumeratedElementHandler(),
					ReferencePropertyIdHandler(),
				),
			)

		// Set up test entity that mimics DocRIC structure
		rootEntity = createDocRICLikeEntity()
	}

	@Test
	fun `partnerList_0_pillarTwoStatementId pattern works end to end`() {
		// The real-world path used in PillarTwoStatementExplainerCalculation
		val path = "partnerList[0].pillarTwoStatementId"

		// Test setting the ID
		val testId = "statement-123-abc"
		pathProcessor.setFieldByPath(path, testId, rootEntity)

		// Test getting the ID back
		val result = pathProcessor.getValueByPath(path, rootEntity)
		assertEquals(testId, result)
	}

	@Test
	fun `partnerList_0_pillarTwoStatementId setting and getting works`() {
		val path = "partnerList[0].pillarTwoStatementId"

		// Set a value and verify it works
		pathProcessor.setFieldByPath(path, "test-id-789", rootEntity)
		assertEquals("test-id-789", pathProcessor.getValueByPath(path, rootEntity))
		
		// Set a different value to verify it updates
		pathProcessor.setFieldByPath(path, "updated-id-456", rootEntity)
		assertEquals("updated-id-456", pathProcessor.getValueByPath(path, rootEntity))
		
		// Note: Null handling behavior is thoroughly covered in unit tests
	}

	@Test
	fun `multiple partners can have different pillarTwoStatementIds`() {
		// Test with two different partners
		val path1 = "partnerList[0].pillarTwoStatementId"
		val path2 = "partnerList[1].pillarTwoStatementId"

		pathProcessor.setFieldByPath(path1, "partner1-statement", rootEntity)
		pathProcessor.setFieldByPath(path2, "partner2-statement", rootEntity)

		assertEquals("partner1-statement", pathProcessor.getValueByPath(path1, rootEntity))
		assertEquals("partner2-statement", pathProcessor.getValueByPath(path2, rootEntity))
	}

	@Test
	fun `mixed property types work together without conflicts`() {
		// Test that enum .id and reference Id patterns don't conflict
		val enumPath = "partnerList[0].gender.id"
		val referencePath = "partnerList[0].pillarTwoStatementId"

		// Set enum via .id suffix (use a valid enum value from PathTestUtils)
		pathProcessor.setFieldByPath(enumPath, "active", rootEntity)

		// Set reference via Id suffix
		pathProcessor.setFieldByPath(referencePath, "stmt-456", rootEntity)

		// Both should work independently
		assertEquals("active", pathProcessor.getValueByPath(enumPath, rootEntity))
		assertEquals("stmt-456", pathProcessor.getValueByPath(referencePath, rootEntity))
	}

	@Test
	fun `complex nested paths work correctly`() {
		// Test simpler nesting that uses existing properties
		val complexPath = "partnerList[0].reference.name"

		// First ensure the structure exists
		pathProcessor.setFieldByPath("partnerList[0].name", "Partner", rootEntity)

		// Set through reference navigation
		pathProcessor.setFieldByPath(complexPath, "updated-ref-name", rootEntity)

		val result = pathProcessor.getValueByPath(complexPath, rootEntity)
		assertEquals("updated-ref-name", result)
	}

	private fun createDocRICLikeEntity(): TestEntity {
		val entity = PathTestUtils.createMockEntity()

		// Add basic properties
		entity.addProperty("name", PathTestUtils.createBaseProperty(String::class.java, "DocRIC"))

		// Add partner list (this is the key structure being tested)
		entity.addProperty("partnerList", PathTestUtils.createPartListProperty())

		return entity
	}

	/**
	 * This test demonstrates that our new handler allows the path to work
	 * without requiring manual property definitions like in DocRICPartPartnerBase
	 */
	@Test
	fun `pillarTwoStatementId works without manual property definition`() {
		// Create a fresh entity without any manual pillarTwoStatementId property
		val cleanEntity = PathTestUtils.createMockEntity()
		cleanEntity.addProperty("partnerList", PathTestUtils.createPartListProperty())

		val path = "partnerList[0].pillarTwoStatementId"

		// This should work purely through the path processing framework
		// without needing the manual property definition in DocRICPartPartnerBase
		assertDoesNotThrow {
			pathProcessor.setFieldByPath(path, "automatic-handling", cleanEntity)
			val result = pathProcessor.getValueByPath(path, cleanEntity)
			assertEquals("automatic-handling", result)
		}
	}
}
