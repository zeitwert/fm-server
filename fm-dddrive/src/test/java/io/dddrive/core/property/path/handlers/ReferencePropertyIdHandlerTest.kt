package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import io.dddrive.core.property.path.PathTestUtils
import io.dddrive.core.property.path.TestEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for ReferencePropertyIdHandler.
 *
 * Tests the handler's ability to process reference property ID access patterns
 * via "Id" suffix and verify it correctly rejects complex paths that should be handled by other handlers.
 */
class ReferencePropertyIdHandlerTest {
	private lateinit var handler: ReferencePropertyIdHandler
	private lateinit var mockContext: MockPathHandlingContext
	private lateinit var testEntity: TestEntity

	@BeforeEach
	fun setUp() {
		handler = ReferencePropertyIdHandler()
		mockContext = MockPathHandlingContext()
		testEntity = PathTestUtils.createMockEntity()
	}

	@Test
	fun `canHandle returns true for reference property with Id suffix`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val canHandle = handler.canHandle("pillarTwoStatementId", null, testEntity)

		assertTrue(canHandle)
	}

	@Test
	fun `canHandle returns false for paths without Id suffix`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val canHandle = handler.canHandle("pillarTwoStatement", referenceProperty, testEntity)

		assertFalse(canHandle)
	}

	@Test
	fun `canHandle returns false for non-reference properties`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")
		testEntity.addProperty("someProperty", baseProperty)

		val canHandle = handler.canHandle("somePropertyId", baseProperty, testEntity)

		assertFalse(canHandle)
	}

	@Test
	fun `canHandle returns false for complex paths with dots`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		// Should return false for paths like "partnerList[0].pillarTwoStatementId"
		val canHandle = handler.canHandle("partnerList[0].pillarTwoStatementId", null, testEntity)

		assertFalse(canHandle, "Should not handle complex paths with dots")
	}

	@Test
	fun `canHandle returns false for nested property paths`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		// Should return false for nested paths
		val canHandle = handler.canHandle("parent.child.pillarTwoStatementId", null, testEntity)

		assertFalse(canHandle, "Should not handle nested property paths")
	}

	@Test
	fun `canHandle returns false for empty base path`() {
		// Test with just "Id" which would result in empty base path
		val canHandle = handler.canHandle("Id", null, testEntity)

		assertFalse(canHandle, "Should not handle empty base path")
	}

	@Test
	fun `canHandle returns false when base property does not exist`() {
		// No property named "nonExistent" exists
		val canHandle = handler.canHandle("nonExistentId", null, testEntity)

		assertFalse(canHandle, "Should not handle when base property does not exist")
	}

	@Test
	fun `canHandle returns false for PartReferenceProperty`() {
		val partReferenceProperty = PathTestUtils.createPartReferenceProperty()
		testEntity.addProperty("somePart", partReferenceProperty)

		val canHandle = handler.canHandle("somePartId", null, testEntity)

		assertFalse(canHandle, "Should not handle PartReferenceProperty (should be handled by PartReferencePropertyHandler)")
	}

	@Test
	fun `handleSet works with valid reference ID`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val testId = "ref-123"
		val result = handler.handleSet("pillarTwoStatementId", testId, null, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertEquals(testId, referenceProperty.id)
	}

	@Test
	fun `handleSet works with null value`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		referenceProperty.id = "existing-id"
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val result = handler.handleSet("pillarTwoStatementId", null, null, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertNull(referenceProperty.id)
	}

	@Test
	fun `handleSet works with different ID types`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		// Test with String ID
		handler.handleSet("pillarTwoStatementId", "string-id", null, testEntity, mockContext)
		assertEquals("string-id", referenceProperty.id)

		// Test with Integer ID
		handler.handleSet("pillarTwoStatementId", 123, null, testEntity, mockContext)
		assertEquals(123, referenceProperty.id)

		// Test with UUID-like string
		val uuid = "550e8400-e29b-41d4-a716-446655440000"
		handler.handleSet("pillarTwoStatementId", uuid, null, testEntity, mockContext)
		assertEquals(uuid, referenceProperty.id)
	}

	@Test
	fun `handleSet throws exception for non-reference property`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")
		testEntity.addProperty("someProperty", baseProperty)

		assertThrows(IllegalArgumentException::class.java) {
			handler.handleSet("somePropertyId", "test-id", null, testEntity, mockContext)
		}
	}

	@Test
	fun `handleGet returns reference ID`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		val testId = "ref-456"
		referenceProperty.id = testId
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val result = handler.handleGet("pillarTwoStatementId", null, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertEquals(testId, result.value)
	}

	@Test
	fun `handleGet returns null for null reference ID`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		referenceProperty.id = null
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val result = handler.handleGet("pillarTwoStatementId", null, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

	@Test
	fun `handleGet returns null for non-reference property`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")
		testEntity.addProperty("someProperty", baseProperty)

		val result = handler.handleGet("somePropertyId", null, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

	@Test
	fun `canHandle returns true only for direct reference property access`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		// Should return true only for direct access
		val canHandleDirect = handler.canHandle("pillarTwoStatementId", null, testEntity)
		val canHandleWithList = handler.canHandle("list[0].pillarTwoStatementId", null, testEntity)
		val canHandleNested = handler.canHandle("obj.pillarTwoStatementId", null, testEntity)

		assertTrue(canHandleDirect, "Should handle direct reference property access")
		assertFalse(canHandleWithList, "Should not handle paths with list notation")
		assertFalse(canHandleNested, "Should not handle nested paths")
	}

	@Test
	fun `handleSet and handleGet work together correctly`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		testEntity.addProperty("pillarTwoStatement", referenceProperty)

		val testId = "round-trip-test-123"

		// Set the ID
		val setResult = handler.handleSet("pillarTwoStatementId", testId, null, testEntity, mockContext)
		assertTrue(setResult.isComplete)

		// Get the ID back
		val getResult = handler.handleGet("pillarTwoStatementId", null, testEntity, mockContext)
		assertTrue(getResult.isComplete)
		assertEquals(testId, getResult.value)
	}

	@Test
	fun `handler correctly identifies reference property types`() {
		val referenceProperty = PathTestUtils.createReferenceProperty()
		val partReferenceProperty = PathTestUtils.createPartReferenceProperty()
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")

		testEntity.addProperty("reference", referenceProperty)
		testEntity.addProperty("partReference", partReferenceProperty)
		testEntity.addProperty("base", baseProperty)

		// Should handle regular ReferenceProperty
		assertTrue(handler.canHandle("referenceId", null, testEntity))

		// Should NOT handle PartReferenceProperty (different handler responsibility)
		assertFalse(handler.canHandle("partReferenceId", null, testEntity))

		// Should NOT handle BaseProperty
		assertFalse(handler.canHandle("baseId", null, testEntity))
	}

	@Test
	fun `multiple reference properties work independently`() {
		val property1 = PathTestUtils.createReferenceProperty()
		val property2 = PathTestUtils.createReferenceProperty()

		testEntity.addProperty("firstReference", property1)
		testEntity.addProperty("secondReference", property2)

		// Set different IDs
		handler.handleSet("firstReferenceId", "id-1", null, testEntity, mockContext)
		handler.handleSet("secondReferenceId", "id-2", null, testEntity, mockContext)

		// Verify independent storage
		assertEquals("id-1", property1.id)
		assertEquals("id-2", property2.id)

		// Verify independent retrieval
		val result1 = handler.handleGet("firstReferenceId", null, testEntity, mockContext)
		val result2 = handler.handleGet("secondReferenceId", null, testEntity, mockContext)

		assertEquals("id-1", result1.value)
		assertEquals("id-2", result2.value)
	}

	/**
	 * Mock implementation of PathHandlingContext for testing.
	 * Returns empty results since this handler doesn't delegate to other handlers.
	 */
	private class MockPathHandlingContext : PathHandlingContext {
		override fun processRemainingPath(
			path: String,
			entity: Any,
			value: Any?,
		): PathHandlingResult = PathHandlingResult.complete()

		override fun getPropertyByPath(
			entity: Any,
			path: String,
		): io.dddrive.core.property.model.Property<*>? = null
	}
}
