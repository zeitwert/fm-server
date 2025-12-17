package io.dddrive.core.property.path

import io.dddrive.core.property.model.Property
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Unit tests for PathElementHandlerRegistry.
 *
 * Tests handler registration, removal, and the two-phase handler selection logic
 * that prevents conflicts between specific handlers.
 */
class PathElementHandlerRegistryTest {
	private lateinit var registry: PathElementHandlerRegistry
	private lateinit var mockHandler1: PathElementHandler
	private lateinit var mockHandler2: PathElementHandler
	private lateinit var mockHandler3: PathElementHandler

	@BeforeEach
	fun setUp() {
		registry = PathElementHandlerRegistry()
		mockHandler1 = createMockHandler("handler1", listOf("path1", "path2"))
		mockHandler2 = createMockHandler("handler2", listOf("path2", "path3"))
		mockHandler3 = createMockHandler("handler3", listOf("path3", "path4"))
	}

	@Test
	fun `registerHandler adds handler to registry`() {
		registry.registerHandler(mockHandler1)

		val handlers = registry.getHandlers()
		assertEquals(1, handlers.size)
		assertEquals(mockHandler1, handlers[0])
	}

	@Test
	fun `registerHandler maintains order of registration`() {
		registry.registerHandler(mockHandler1)
		registry.registerHandler(mockHandler2)
		registry.registerHandler(mockHandler3)

		val handlers = registry.getHandlers()
		assertEquals(3, handlers.size)
		assertEquals(mockHandler1, handlers[0])
		assertEquals(mockHandler2, handlers[1])
		assertEquals(mockHandler3, handlers[2])
	}

	@Test
	fun `removeHandler removes specified handler`() {
		registry.registerHandler(mockHandler1)
		registry.registerHandler(mockHandler2)
		registry.registerHandler(mockHandler3)

		val removed = registry.removeHandler(mockHandler2)

		assertTrue(removed)
		val handlers = registry.getHandlers()
		assertEquals(2, handlers.size)
		assertEquals(mockHandler1, handlers[0])
		assertEquals(mockHandler3, handlers[1])
	}

	@Test
	fun `removeHandler returns false for non-existent handler`() {
		registry.registerHandler(mockHandler1)

		val removed = registry.removeHandler(mockHandler2)

		assertFalse(removed)
		assertEquals(1, registry.getHandlers().size)
	}

	@Test
	fun `findHandler throws exception when multiple handlers match`() {
		registry.registerHandler(mockHandler1) // Handles path2
		registry.registerHandler(mockHandler2) // Also handles path2

		assertThrows(IllegalStateException::class.java) {
			registry.findHandler("path2", null, "testEntity")
		}
	}

	@Test
	fun `findHandler returns null when no handler matches`() {
		registry.registerHandler(mockHandler1)
		registry.registerHandler(mockHandler2)

		val handler = registry.findHandler("unknownPath", null, "testEntity")

		assertNull(handler)
	}

	@Test
	fun `findHandler returns correct handler for unique path`() {
		registry.registerHandler(mockHandler1)
		registry.registerHandler(mockHandler2)
		registry.registerHandler(mockHandler3)

		val handler = registry.findHandler("path4", null, "testEntity")

		assertEquals(mockHandler3, handler)
	}

	@Test
	fun `getHandlers returns copy of handlers list`() {
		registry.registerHandler(mockHandler1)
		registry.registerHandler(mockHandler2)

		val handlers1 = registry.getHandlers()
		val handlers2 = registry.getHandlers()

		// Should be different instances but same content
		assertNotSame(handlers1, handlers2)
		assertEquals(handlers1, handlers2)
	}

	@Test
	fun `clear removes all handlers`() {
		registry.registerHandler(mockHandler1)
		registry.registerHandler(mockHandler2)
		registry.registerHandler(mockHandler3)

		assertEquals(3, registry.getHandlers().size)

		registry.clear()

		assertEquals(0, registry.getHandlers().size)
	}

	@Test
	fun `registry works correctly after clear and re-register`() {
		registry.registerHandler(mockHandler1)
		registry.clear()
		registry.registerHandler(mockHandler2)

		val handlers = registry.getHandlers()
		assertEquals(1, handlers.size)
		assertEquals(mockHandler2, handlers[0])

		val handler = registry.findHandler("path3", null, "testEntity")
		assertEquals(mockHandler2, handler)
	}

	private fun createMockHandler(
		name: String,
		handledPaths: List<String>,
	): PathElementHandler =
		object : PathElementHandler {
			override fun canHandle(
				path: String,
				property: Property<*>?,
				entity: Any,
			): Boolean = handledPaths.contains(path)

			override fun handleSet(
				path: String,
				value: Any?,
				property: Property<*>?,
				entity: Any,
				context: PathHandlingContext,
			): PathHandlingResult = PathHandlingResult.complete()

			override fun handleGet(
				path: String,
				property: Property<*>?,
				entity: Any,
				context: PathHandlingContext,
			): PathHandlingResult = PathHandlingResult.complete("$name-result")

			override fun toString(): String = name
		}
}
