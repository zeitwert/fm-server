package io.zeitwert.fm.ddd.model

/**
 * Interface for entities that support extension maps containing additional, non-standard properties.
 * This allows flexible storage of custom attributes without schema changes.
 */
interface EntityWithExtn {

	/**
	 * Retrieves extension map containing additional, non-standard properties.
	 *
	 * @return Map<String, Any> The map of extension properties, or null if not set.
	 */
	val extnMap: Map<String, Any>

	/**
	 * Checks if a specific key exists in the extension map.
	 *
	 * @param key The key to check in the extension map.
	 * @return Boolean True if the key exists, false otherwise.
	 */
	fun hasExtn(key: String): Boolean

	/**
	 * Retrieves the value associated with a specific key in the extension map.
	 *
	 * @param key The key whose value is to be retrieved.
	 * @return Any? The value associated with the specified key.
	 */
	fun getExtn(key: String): Any?

	/**
	 * Sets or updates the value associated with a specific key in the extension map.
	 *
	 * @param key The key for which the value is to be set.
	 * @param value The value to be associated with the specified key.
	 */
	fun setExtn(
		key: String,
		value: Any?,
	)

}
