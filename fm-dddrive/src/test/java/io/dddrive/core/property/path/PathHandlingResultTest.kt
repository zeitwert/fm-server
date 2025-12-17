package io.dddrive.core.property.path

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for PathHandlingResult data class.
 *
 * Tests the factory methods, data class functionality, and behavior
 * of the result object used in path processing operations.
 */
class PathHandlingResultTest {

	@Test
	fun `complete with null value creates completed result`() {
		val result = PathHandlingResult.complete()

		assertTrue(result.isComplete)
		assertNull(result.value)
		assertNull(result.continueWithPath)
		assertNull(result.continueWithEntity)
	}

	@Test
	fun `complete with value creates completed result with value`() {
		val testValue = "testValue"
		val result = PathHandlingResult.complete(testValue)

		assertTrue(result.isComplete)
		assertEquals(testValue, result.value)
		assertNull(result.continueWithPath)
		assertNull(result.continueWithEntity)
	}

	@Test
	fun `complete with different value types works correctly`() {
		// Test with String
		val stringResult = PathHandlingResult.complete("test")
		assertEquals("test", stringResult.value)

		// Test with Integer
		val intResult = PathHandlingResult.complete(42)
		assertEquals(42, intResult.value)

		// Test with Boolean
		val booleanResult = PathHandlingResult.complete(true)
		assertEquals(true, booleanResult.value)

		// Test with complex object
		val objectResult = PathHandlingResult.complete(listOf(1, 2, 3))
		assertEquals(listOf(1, 2, 3), objectResult.value)
	}

	@Test
	fun `continueWith creates continuation result`() {
		val testPath = "remaining.path"
		val testEntity = "testEntity"

		val result = PathHandlingResult.continueWith(testPath, testEntity)

		assertFalse(result.isComplete)
		assertNull(result.value)
		assertEquals(testPath, result.continueWithPath)
		assertEquals(testEntity, result.continueWithEntity)
	}

	@Test
	fun `continueWith with different entity types works correctly`() {
		val path = "test.path"

		// Test with String entity
		val stringResult = PathHandlingResult.continueWith(path, "stringEntity")
		assertEquals("stringEntity", stringResult.continueWithEntity)

		// Test with complex object entity
		val complexEntity = mapOf("key" to "value")
		val complexResult = PathHandlingResult.continueWith(path, complexEntity)
		assertEquals(complexEntity, complexResult.continueWithEntity)
	}

	@Test
	fun `data class properties work correctly`() {
		val result1 =
			PathHandlingResult(
				isComplete = true,
				value = "testValue",
				continueWithPath = null,
				continueWithEntity = null,
			)

		val result2 =
			PathHandlingResult(
				isComplete = false,
				value = null,
				continueWithPath = "test.path",
				continueWithEntity = "testEntity",
			)

		// Test property access
		assertTrue(result1.isComplete)
		assertEquals("testValue", result1.value)
		assertNull(result1.continueWithPath)
		assertNull(result1.continueWithEntity)

		assertFalse(result2.isComplete)
		assertNull(result2.value)
		assertEquals("test.path", result2.continueWithPath)
		assertEquals("testEntity", result2.continueWithEntity)
	}

	@Test
	fun `equals and hashCode work correctly for data class`() {
		val result1 = PathHandlingResult.complete("test")
		val result2 = PathHandlingResult.complete("test")
		val result3 = PathHandlingResult.complete("different")
		val result4 = PathHandlingResult.continueWith("path", "entity")
		val result5 = PathHandlingResult.continueWith("path", "entity")

		// Test equality
		assertEquals(result1, result2)
		assertNotEquals(result1, result3)
		assertEquals(result4, result5)
		assertNotEquals(result1, result4)

		// Test hashCode consistency
		assertEquals(result1.hashCode(), result2.hashCode())
		assertEquals(result4.hashCode(), result5.hashCode())
	}

	@Test
	fun `toString provides useful information`() {
		val completeResult = PathHandlingResult.complete("testValue")
		val continueResult = PathHandlingResult.continueWith("test.path", "testEntity")

		val completeString = completeResult.toString()
		val continueString = continueResult.toString()

		// Should contain key information
		assertTrue(completeString.contains("isComplete=true"))
		assertTrue(completeString.contains("testValue"))

		assertTrue(continueString.contains("isComplete=false"))
		assertTrue(continueString.contains("test.path"))
		assertTrue(continueString.contains("testEntity"))
	}

	@Test
	fun `copy method works correctly for data class`() {
		val original =
			PathHandlingResult(
				isComplete = false,
				value = "originalValue",
				continueWithPath = "original.path",
				continueWithEntity = "originalEntity",
			)

		// Test copying with changes
		val modified = original.copy(isComplete = true, value = "newValue")

		assertTrue(modified.isComplete)
		assertEquals("newValue", modified.value)
		assertEquals("original.path", modified.continueWithPath) // unchanged
		assertEquals("originalEntity", modified.continueWithEntity) // unchanged

		// Original should be unchanged
		assertFalse(original.isComplete)
		assertEquals("originalValue", original.value)
	}

	@Test
	fun `companion object methods are convenient factory methods`() {
		val completeResult = PathHandlingResult.complete("test")
		val manualComplete = PathHandlingResult(isComplete = true, value = "test")

		val continueResult = PathHandlingResult.continueWith("path", "entity")
		val manualContinue =
			PathHandlingResult(
				isComplete = false,
				continueWithPath = "path",
				continueWithEntity = "entity",
			)

		assertEquals(completeResult, manualComplete)
		assertEquals(continueResult, manualContinue)
	}
}
