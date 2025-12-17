package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.path.PathHandlingResult
import io.dddrive.core.property.path.PathTestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for DefaultPropertyHandler
 */
class DefaultPropertyHandlerTest {

	private lateinit var handler: DefaultPropertyHandler
	private lateinit var mockContext: MockPathHandlingContext

	@BeforeEach
	fun setUp() {
		handler = DefaultPropertyHandler()
		mockContext = MockPathHandlingContext()
	}

	@Test
	fun `canHandle returns true for simple property names with BaseProperty`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")

		val canHandle = handler.canHandle("simpleProp", baseProperty, "testEntity")

		assertTrue(canHandle)
	}

	@Test
	fun `canHandle returns true for simple property names with EnumProperty`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)

		val canHandle = handler.canHandle("enumProp", enumProperty, "testEntity")

		assertTrue(canHandle)
	}

	@Test
	fun `canHandle returns false for paths with dots`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")

		val canHandle = handler.canHandle("complex.path", baseProperty, "testEntity")

		assertFalse(canHandle)
	}

	@Test
	fun `canHandle returns false for paths with brackets`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "test")

		val canHandle = handler.canHandle("list[0]", baseProperty, "testEntity")

		assertFalse(canHandle)
	}

	@Test
	fun `canHandle returns false for unsupported property types`() {
		val unsupportedProperty =
			object : io.dddrive.core.property.model.Property<String> {
				override val entity: io.dddrive.core.property.model.EntityWithProperties = PathTestUtils.createMockEntity()

				override val relativePath: String = "test"

				override val path: String = "test"

				override val name: String = "test"

				override val isWritable: Boolean = true
			}

		val canHandle = handler.canHandle("prop", unsupportedProperty, "testEntity")

		assertFalse(canHandle)
	}

	@Test
	fun `canHandle returns false for null property`() {
		val canHandle = handler.canHandle("prop", null, "testEntity")

		assertFalse(canHandle)
	}

	@Test
	fun `handleSet works with BaseProperty and string value`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "oldValue")

		val result = handler.handleSet("prop", "newValue", baseProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertEquals("newValue", baseProperty.value)
	}

	@Test
	fun `handleSet works with BaseProperty and null value`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "oldValue")

		val result = handler.handleSet("prop", null, baseProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertNull(baseProperty.value)
	}

	@Test
	fun `handleSet works with BaseProperty and different value types`() {
		val intProperty = PathTestUtils.createBaseProperty(Int::class.java, 10)
		val boolProperty = PathTestUtils.createBaseProperty(Boolean::class.java, false)

		handler.handleSet("intProp", 42, intProperty, "testEntity", mockContext)
		handler.handleSet("boolProp", true, boolProperty, "testEntity", mockContext)

		assertEquals(42, intProperty.value)
		assertEquals(true, boolProperty.value)
	}

	@Test
	fun `handleSet works with EnumProperty and Enumerated value`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)
		val enumValue = enumeration.getItem("value1")!!

		val result = handler.handleSet("enumProp", enumValue, enumProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertEquals(enumValue, enumProperty.value)
	}

	@Test
	fun `handleSet works with EnumProperty and string ID`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)

		val result = handler.handleSet("enumProp", "value2", enumProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertEquals("value2", enumProperty.value?.id)
	}

	@Test
	fun `handleSet works with EnumProperty and null value`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration, enumeration.getItem("value1"))

		val result = handler.handleSet("enumProp", null, enumProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertNull(enumProperty.value)
	}

	@Test
	fun `handleSet throws exception for EnumProperty with invalid value type`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration)

		assertThrows(IllegalArgumentException::class.java) {
			handler.handleSet("enumProp", 123, enumProperty, "testEntity", mockContext)
		}
	}

	@Test
	fun `handleSet throws exception for unsupported property type`() {
		val unsupportedProperty =
			object : io.dddrive.core.property.model.Property<String> {
				override val entity: io.dddrive.core.property.model.EntityWithProperties = PathTestUtils.createMockEntity()

				override val relativePath: String = "test"

				override val path: String = "test"

				override val name: String = "test"

				override val isWritable: Boolean = true
			}

		assertThrows(IllegalArgumentException::class.java) {
			handler.handleSet("prop", "value", unsupportedProperty, "testEntity", mockContext)
		}
	}

	@Test
	fun `handleGet returns value from BaseProperty`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")

		val result = handler.handleGet("prop", baseProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertEquals("testValue", result.value)
	}

	@Test
	fun `handleGet returns null from BaseProperty with null value`() {
		val baseProperty = PathTestUtils.createBaseProperty(String::class.java, null)

		val result = handler.handleGet("prop", baseProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

	@Test
	fun `handleGet returns value from EnumProperty`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumValue = enumeration.getItem("value1")!!
		val enumProperty = PathTestUtils.createEnumProperty(enumeration, enumValue)

		val result = handler.handleGet("enumProp", enumProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertEquals(enumValue, result.value)
	}

	@Test
	fun `handleGet returns null from EnumProperty with null value`() {
		val enumeration = PathTestUtils.createMockEnumeration()
		val enumProperty = PathTestUtils.createEnumProperty(enumeration, null)

		val result = handler.handleGet("enumProp", enumProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

	@Test
	fun `handleGet returns null for unsupported property type`() {
		val unsupportedProperty =
			object : io.dddrive.core.property.model.Property<String> {
				override val entity: io.dddrive.core.property.model.EntityWithProperties = PathTestUtils.createMockEntity()

				override val relativePath: String = "test"

				override val path: String = "test"

				override val name: String = "test"

				override val isWritable: Boolean = true
			}

		val result = handler.handleGet("prop", unsupportedProperty, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

	@Test
	fun `handleGet returns null for null property`() {
		val result = handler.handleGet("prop", null, "testEntity", mockContext)

		assertTrue(result.isComplete)
		assertNull(result.value)
	}

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
