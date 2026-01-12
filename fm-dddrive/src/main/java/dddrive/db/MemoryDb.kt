package dddrive.db

import dddrive.query.ComparisonOperator
import dddrive.query.FilterSpec
import dddrive.query.QuerySpec

/**
 * Generic in-memory database singleton for storing objects as Map<String, Any?>.
 *
 * Supports storing, retrieving by ID, and querying with QuerySpec filters.
 * Objects are organized by type (Class) to enable type-specific queries.
 */
object MemoryDb {

	// Storage: type -> (id -> map)
	private val storage: MutableMap<Class<*>, MutableMap<Int, Map<String, Any?>>> = HashMap()

	/**
	 * Stores an object (as a map) under the given type.
	 * The map must contain an "id" key with an Int value.
	 *
	 * @param type The type/class to store the object under
	 * @param map The object data as a map (must contain "id" key)
	 */
	fun store(
		type: Class<*>,
		map: Map<String, Any?>,
	) {
		val id = map["id"] as? Int ?: error("Map must contain 'id' key with Int value")
		storage.getOrPut(type) { HashMap() }[id] = map
	}

	/**
	 * Retrieves an object by type and ID.
	 *
	 * @param type The type/class the object was stored under
	 * @param id The object's ID
	 * @return The object data as a map, or null if not found
	 */
	fun get(
		type: Class<*>,
		id: Int,
	): Map<String, Any?>? = storage[type]?.get(id)

	/**
	 * Finds objects matching the given query specification.
	 *
	 * @param type The type to search in, or null to search across all types
	 * @param query The query specification with filters, or null to return all objects
	 * @return List of matching objects as maps
	 */
	fun find(
		type: Class<*>?,
		query: QuerySpec?,
	): List<Map<String, Any?>> {
		val candidates =
			if (type != null) {
				storage[type]?.values ?: emptyList()
			} else {
				storage.values.flatMap { it.values }
			}

		if (query == null || query.filters.isEmpty()) {
			return candidates.toList()
		}

		return candidates
			.filter { map -> query.filters.all { filter -> matchesFilter(map, filter) } }
			.toList()
	}

	/**
	 * Clears all stored data.
	 * Useful for resetting state between tests.
	 */
	fun clear() {
		storage.clear()
	}

	/**
	 * Clears stored data for a specific type.
	 *
	 * @param type The type to clear
	 */
	fun clear(type: Class<*>) {
		storage.remove(type)
	}

	private fun matchesFilter(
		map: Map<String, Any?>,
		filter: FilterSpec,
	): Boolean =
		when (filter) {
			is FilterSpec.Comparison -> {
				val path = filter.path
				val mapKey = if (path.endsWith("Id")) path else "${path}Id"
				val mapValue = map[path] ?: map[mapKey]
				when (filter.operator) {
					ComparisonOperator.EQ -> mapValue == filter.value
					ComparisonOperator.NEQ -> mapValue != filter.value
					ComparisonOperator.GT -> compareValues(mapValue, filter.value) > 0
					ComparisonOperator.GE -> compareValues(mapValue, filter.value) >= 0
					ComparisonOperator.LT -> compareValues(mapValue, filter.value) < 0
					ComparisonOperator.LE -> compareValues(mapValue, filter.value) <= 0
					ComparisonOperator.LIKE -> matchesLike(mapValue, filter.value)
				}
			}

			is FilterSpec.In -> {
				val mapValue = map[filter.path]
				mapValue in filter.values
			}

			is FilterSpec.Or -> {
				filter.filters.any { matchesFilter(map, it) }
			}
		}

	@Suppress("UNCHECKED_CAST")
	private fun compareValues(
		a: Any?,
		b: Any?,
	): Int {
		if (a == null && b == null) return 0
		if (a == null) return -1
		if (b == null) return 1
		return (a as Comparable<Any>).compareTo(b)
	}

	private fun matchesLike(
		value: Any?,
		pattern: Any?,
	): Boolean {
		if (value == null || pattern == null) return false
		val valueStr = value.toString()
		val patternStr = pattern.toString()
		// Convert * wildcard to regex .* and escape other regex chars
		val regex =
			patternStr
				.replace(".", "\\.")
				.replace("*", ".*")
				.toRegex(RegexOption.IGNORE_CASE)
		return regex.matches(valueStr)
	}

}
