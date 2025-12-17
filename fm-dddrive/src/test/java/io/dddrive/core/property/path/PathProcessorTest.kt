package io.dddrive.core.property.path

import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.Property
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for PathProcessor.
 *
 * Tests the core path processing logic using mock handlers to verify:
 * - Handler delegation works correctly
 * - Path segment extraction functions properly
 * - Error handling for invalid paths and entities
 */
class PathProcessorTest {

	private lateinit var pathProcessor: PathProcessor
	private lateinit var mockHandlers: List<PathElementHandler>
	private lateinit var testEntity: TestEntity

	@BeforeEach
	fun setUp() {
		// Create mock handlers for testing
		mockHandlers =
			listOf(
				createMockHandler("handler1", "path1", "result1"),
				createMockHandler("handler2", "path2", "result2"),
				createMockHandler("handler3", "path3", "result3"),
			)

		// Create PathProcessor with mock handlers
		pathProcessor = PathProcessor(mockHandlers)

		// Create test entity
		testEntity = PathTestUtils.createMockEntity()
	}

	@Test
	fun `constructor registers all provided handlers`() {
		val registeredHandlers = pathProcessor.getRegisteredHandlers()

		assertEquals(3, registeredHandlers.size)
		assertEquals(mockHandlers[0], registeredHandlers[0])
		assertEquals(mockHandlers[1], registeredHandlers[1])
		assertEquals(mockHandlers[2], registeredHandlers[2])
	}

	@Test
	fun `getValueByPath returns null for empty path`() {
		val result = pathProcessor.getValueByPath("", testEntity)

		assertEquals(testEntity, result)
	}

	@Test
	fun `getValueByPath delegates to correct handler`() {
		// Add a property that will be found by the handler
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("path2", testProperty)

		val result = pathProcessor.getValueByPath("path2", testEntity)

		assertEquals("result2", result)
	}

	@Test
	fun `getValueByPath throws exception when no handler found`() {
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("unknownPath", testProperty)

		assertThrows(IllegalArgumentException::class.java) {
			pathProcessor.getValueByPath("unknownPath", testEntity)
		}
	}

	@Test
	fun `setFieldByPath delegates to correct handler`() {
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "oldValue")
		testEntity.addProperty("path1", testProperty)

		// This should not throw an exception
		assertDoesNotThrow {
			pathProcessor.setFieldByPath("path1", "newValue", testEntity)
		}
	}

	@Test
	fun `setFieldByPath throws exception when no handler found`() {
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("unknownPath", testProperty)

		assertThrows(IllegalArgumentException::class.java) {
			pathProcessor.setFieldByPath("unknownPath", "newValue", testEntity)
		}
	}

	@Test
	fun `processRemainingPath works correctly as PathHandlingContext`() {
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("path3", testProperty)

		val result = pathProcessor.processRemainingPath("path3", testEntity, "setValue")

		assertTrue(result.isComplete)
		assertEquals("setValue", result.value) // Mock handler returns the set value
	}

	@Test
	fun `getPropertyByPath works for EntityWithProperties`() {
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("testProp", testProperty)

		val property = pathProcessor.getPropertyByPath(testEntity, "testProp")

		assertEquals(testProperty, property)
	}

	@Test
	fun `getPropertyByPath throws exception for non-EntityWithProperties`() {
		val nonEntityObject = "notAnEntity"

		assertThrows(IllegalArgumentException::class.java) {
			pathProcessor.getPropertyByPath(nonEntityObject, "testProp")
		}
	}

	@Test
	fun `extractFirstSegment handles simple property names`() {
		// This tests the private method indirectly through public API
		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("simpleProp", testProperty)

		assertDoesNotThrow {
			pathProcessor.getValueByPath("path1", testEntity)
		}
	}

	@Test
	fun `expandListsOnPath handles list expansion`() {
		// For this test, we need to use real handlers that can process list notation
		val realHandlers =
			listOf(
				io.dddrive.core.property.path.handlers
					.ListPathElementHandler(),
				io.dddrive.core.property.path.handlers
					.DefaultPropertyHandler(),
			)
		val realPathProcessor = PathProcessor(realHandlers)

		// Create a custom list property that creates parts with the needed property
		val listProperty =
			object : PartListProperty<TestPart> {

				private val partList = mutableListOf<TestPart>()
				private var nextId = 1

				override val partType = TestPart::class.java

				override val partCount: Int get() = partList.size

				override fun getPart(seqNr: Int): TestPart = partList[seqNr]

				override fun getPartById(partId: Int): TestPart = partList.find { it.id == partId }!!

				override val parts: List<TestPart> get() = partList.toList()

				override fun clearParts() {
					partList.clear()
				}

				override fun addPart(partId: Int?): TestPart {
					val id = partId ?: nextId++
					val newPart = TestPart(id)
					// Add the path1 property that the test expects
					newPart.addProperty("path1", PathTestUtils.createBaseProperty(String::class.java, null))
					partList.add(newPart)
					return newPart
				}

				override fun removePart(partId: Int) {
					partList.removeIf { it.id == partId }
				}

				override fun removePart(part: TestPart) {
					partList.remove(part)
				}

				override fun getIndexOfPart(part: io.dddrive.core.ddd.model.Part<*>): Int = parts.indexOfFirst { it.id == part.id }

				override val entity: EntityWithProperties = testEntity

				override val relativePath: String = "testList"

				override val path: String = "testList"

				override val name: String = "testList"

				override val isWritable: Boolean = true
			}

		testEntity.addProperty("testList", listProperty)

		// This should expand the list and not throw an exception
		assertDoesNotThrow {
			realPathProcessor.setFieldByPath("testList[0].path1", "testValue", testEntity)
		}

		// Verify list was expanded
		assertEquals(1, listProperty.partCount)

		// Verify the value was set correctly
		val part = listProperty.getPart(0)
		val property = part.getProperty("path1")
		assertTrue(
			property is io.dddrive.core.property.model.BaseProperty<*>,
			"Property 'path1' should be of type BaseProperty",
		)
		val path1Property = property as io.dddrive.core.property.model.BaseProperty<String>
		assertEquals(String::class.java, path1Property.type, "Property should be of type String")
		assertEquals("testValue", path1Property.value)
	}

	@Test
	fun `continuation processing works correctly`() {
		// Create a handler that returns continuation
		val continuationHandler =
			object : PathElementHandler {
				override fun canHandle(
					path: String,
					property: Property<*>?,
					entity: Any,
				): Boolean = path == "continuePath"

				override fun handleGet(
					path: String,
					property: Property<*>?,
					entity: Any,
					context: PathHandlingContext,
				): PathHandlingResult {
					// Create a new entity for continuation
					val newEntity = PathTestUtils.createMockEntity()
					val newProperty = PathTestUtils.createBaseProperty(String::class.java, "continuationResult")
					newEntity.addProperty("path1", newProperty)

					return PathHandlingResult.continueWith("path1", newEntity)
				}

				override fun handleSet(
					path: String,
					value: Any?,
					property: Property<*>?,
					entity: Any,
					context: PathHandlingContext,
				): PathHandlingResult = PathHandlingResult.complete()
			}

		// Create processor with continuation handler
		val processorWithContinuation = PathProcessor(mockHandlers + continuationHandler)

		val testProperty = PathTestUtils.createBaseProperty(String::class.java, "testValue")
		testEntity.addProperty("continuePath", testProperty)

		val result = processorWithContinuation.getValueByPath("continuePath", testEntity)

		assertEquals("result1", result) // Should get result from handler1 processing "path1"
	}

	private fun createMockHandler(
		name: String,
		handledPath: String,
		returnValue: String,
	): PathElementHandler =
		object : PathElementHandler {
			override fun canHandle(
				path: String,
				property: Property<*>?,
				entity: Any,
			): Boolean = path == handledPath

			override fun handleSet(
				path: String,
				value: Any?,
				property: Property<*>?,
				entity: Any,
				context: PathHandlingContext,
			): PathHandlingResult = PathHandlingResult.complete(value)

			override fun handleGet(
				path: String,
				property: Property<*>?,
				entity: Any,
				context: PathHandlingContext,
			): PathHandlingResult = PathHandlingResult.complete(returnValue)

			override fun toString(): String = name
		}
}
