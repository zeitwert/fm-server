package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.path.PathElementHandler
import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import org.springframework.stereotype.Component

/**
 * Default fallback handler for standard property access
 * Only activated on final path segments (no suffixes or complex navigation)
 * Handles BaseProperty and EnumProperty direct access
 */
@Component
class DefaultPropertyHandler : PathElementHandler {

	override fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean = property != null && !path.contains('.') && !path.contains('[') && (property is BaseProperty<*>)

	override fun handleSet(
		path: String,
		value: Any?,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		when (property) {
			is EnumProperty<*> -> {
				@Suppress("UNCHECKED_CAST")
				val enumProperty = property as EnumProperty<io.dddrive.core.enums.model.Enumerated>
				enumProperty.value =
					when (value) {
						null -> null

						is io.dddrive.core.enums.model.Enumerated -> value

						is String -> enumProperty.enumeration.getItem(value)

						else -> throw IllegalArgumentException(
							"Cannot set enum property to value of type ${value.javaClass.simpleName}. " +
								"Expected Enumerated instance or String ID.",
						)
					}
			}

			is BaseProperty<*> -> {
				@Suppress("UNCHECKED_CAST")
				(property as BaseProperty<Any>).value = value
			}

			else -> {
				throw IllegalArgumentException("Unsupported property type: ${property?.javaClass?.simpleName ?: "unknown"}")
			}
		}

		return PathHandlingResult.complete()
	}

	override fun handleGet(
		path: String,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult =
		when (property) {
			is EnumProperty<*> -> PathHandlingResult.complete(property.value)
			is BaseProperty<*> -> PathHandlingResult.complete(property.value)
			else -> PathHandlingResult.complete(null)
		}
}
