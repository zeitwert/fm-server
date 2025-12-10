package io.dddrive.core.property.path

/**
 * Result of a path element handling operation.
 *
 * Handlers return this to indicate either:
 * - Processing is complete with a final value
 * - Processing should continue with a remaining path on a different entity
 *
 * @param isComplete true if processing is complete, false if continuation is needed
 * @param value the value retrieved (for get operations) or null
 * @param continueWithPath remaining path to process (for continuation)
 * @param continueWithEntity entity to continue processing with (for continuation)
 */
data class PathHandlingResult(
	val isComplete: Boolean,
	val value: Any? = null,
	val continueWithPath: String? = null,
	val continueWithEntity: Any? = null,
) {
	companion object {
		/**
		 * Creates a result indicating processing is complete.
		 *
		 * @param value the final value (for get operations)
		 */
		fun complete(value: Any? = null) = PathHandlingResult(isComplete = true, value = value)

		/**
		 * Creates a result indicating processing should continue with the remaining path.
		 *
		 * @param path remaining path to process
		 * @param entity entity to continue processing on
		 */
		fun continueWith(
			path: String,
			entity: Any,
		) = PathHandlingResult(
			isComplete = false,
			continueWithPath = path,
			continueWithEntity = entity,
		)
	}
}
