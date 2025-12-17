package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.path.PathHandlingResult
import io.dddrive.core.property.path.PathTestUtils
import io.dddrive.core.property.path.TestEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for EnumeratedElementHandler.
 *
 * Tests the handler's ability to process enum property .id access patterns
 * and verify it correctly rejects complex paths that should be handled by other handlers.
 */
class EnumeratedElementHandlerTest {
	private lateinit var handler: EnumeratedElementHandler
	private lateinit var mockContext: MockPathHandlingContext
	private lateinit var testEntity: TestEntity

	@BeforeEach
	fun setUp() {
		handler = EnumeratedElementHandler()
		mockContext = MockPathHandlingContext()
		testEntity = PathTestUtils.createMockEntity()
	}

	@Test
	fun `canHandle returns true for enum property with id suffix`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		testEntity.addProperty("status", enumProperty)

		val canHandle = handler.canHandle("status.id", enumProperty, testEntity)

		assertTrue(canHandle)
	}

	@Test
	fun `canHandle returns false for paths without id suffix`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)

		val canHandle = handler.canHandle("status", enumProperty, testEntity)

		assertFalse(canHandle)
	}

	@Test
	fun `canHandle returns false for non-enum properties`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")

		val canHandle = handler.canHandle("prop.id", baseProperty, testEntity)

		assertFalse(canHandle)
	}

	@Test
	fun `handleSet works with string enum ID`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		testEntity.addProperty("status", enumProperty)

		val result = handler.handleSet("status.id", "value1", enumProperty, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertEquals("value1", enumProperty.value?.getId())
	}

	@Test
	fun `handleSet works with null value`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration, enumeration.getItem("value1"))
		testEntity.addProperty("status", enumProperty)

		val result = handler.handleSet("status.id", null, enumProperty, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertNull(enumProperty.value)
	}

	@Test
	fun `handleSet throws exception for non-string value`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		testEntity.addProperty("status", enumProperty)

		assertThrows(IllegalArgumentException::class.java) {
			handler.handleSet("status.id", 123, enumProperty, testEntity, mockContext)
		}
	}

	@Test
	fun `handleGet returns enum ID`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumValue = enumeration.getItem("value2")!!
		val enumProperty = PathTestUtils.createEnumProperty(enumeration, enumValue)
		testEntity.addProperty("status", enumProperty)

		val result = handler.handleGet("status.id", enumProperty, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertEquals("value2", result.value)
	}

	@Test
	fun `handleGet returns null for null enum value`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration, null)
		testEntity.addProperty("status", enumProperty)

		val result = handler.handleGet("status.id", enumProperty, testEntity, mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

	@Test
	fun `canHandle returns false for complex paths with dots`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		testEntity.addProperty("gender", enumProperty)

		// Should return false for paths like "partnerList[0].gender.id"
		val canHandle = handler.canHandle("partnerList[0].gender.id", enumProperty, testEntity)

		assertFalse(canHandle, "Should not handle complex paths with dots before the enum property")
	}

	@Test
	fun `canHandle returns false for nested property paths`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		testEntity.addProperty("status", enumProperty)

		// Should return false for nested paths
		val canHandle = handler.canHandle("parent.child.status.id", enumProperty, testEntity)

		assertFalse(canHandle, "Should not handle nested property paths")
	}

	@Test
	fun `canHandle returns true only for direct enum property access`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		testEntity.addProperty("gender", enumProperty)

		// Should return true only for direct access
		val canHandleDirect = handler.canHandle("gender.id", enumProperty, testEntity)
		val canHandleWithList = handler.canHandle("list[0].gender.id", enumProperty, testEntity)
		val canHandleNested = handler.canHandle("obj.gender.id", enumProperty, testEntity)

		assertTrue(canHandleDirect, "Should handle direct enum property access")
		assertFalse(canHandleWithList, "Should not handle paths with list notation")
		assertFalse(canHandleNested, "Should not handle nested paths")
	}

	/**
	 * Mock implementation of PathHandlingContext for testing.
	 * Returns empty results since this handler doesn't delegate to other handlers.
	 */
	private class MockPathHandlingContext : io.dddrive.core.property.path.PathHandlingContext {
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
