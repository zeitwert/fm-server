package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.path.PathElementHandler
import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import org.springframework.stereotype.Component

/**
 * Handles enum properties with .id suffix access for direct property paths.
 *
 * Converts between enum instances and their string IDs. Only handles simple
 * property names (e.g., "status.id") and delegates complex paths to other
 * handlers first (e.g., "partnerList[0].gender.id" is handled by list handler first).
 */
@Component
class EnumeratedElementHandler : PathElementHandler {

	private companion object {

		private const val ID_SUFFIX = ".id"
	}

	override fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean {
		if (!path.endsWith(ID_SUFFIX)) return false

		val basePath = path.removeSuffix(ID_SUFFIX)
		if (basePath.contains('.')) return false

		return try {
			val baseProperty = getBaseProperty(basePath, entity)
			baseProperty is EnumProperty<*>
		} catch (_: Exception) {
			false
		}
	}

	override fun handleSet(
		path: String,
		value: Any?,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		if (!path.endsWith(ID_SUFFIX)) {
			return PathHandlingResult.complete()
		}

		val basePath = path.removeSuffix(ID_SUFFIX)
		val baseProperty = getBaseProperty(basePath, entity)

		if (baseProperty !is EnumProperty<*>) {
			throw IllegalArgumentException("Property at path '$basePath' is not an EnumProperty, required for .id handling.")
		}

		@Suppress("UNCHECKED_CAST")
		val enumProperty = baseProperty as EnumProperty<io.dddrive.core.enums.model.Enumerated>

		enumProperty.value =
			when (value) {
				null -> null
				is String -> enumProperty.enumeration.getItem(value)
				else -> throw IllegalArgumentException("Enum ID value must be String, got: ${value.javaClass.simpleName}")
			}

		return PathHandlingResult.complete()
	}

	override fun handleGet(
		path: String,
		property: Property<*>?,
		entity: Any,
		context: PathHandlingContext,
	): PathHandlingResult {
		if (!path.endsWith(ID_SUFFIX)) {
			return PathHandlingResult.complete()
		}

		val basePath = path.removeSuffix(ID_SUFFIX)
		val baseProperty = getBaseProperty(basePath, entity)

		if (baseProperty !is EnumProperty<*>) {
			return PathHandlingResult.complete(null)
		}

		val enumValue = baseProperty.value
		return PathHandlingResult.complete(enumValue?.id)
	}

	private fun getBaseProperty(
		basePath: String,
		entity: Any,
	): Property<*>? =
		when (entity) {
			is EntityWithProperties -> entity.getProperty(basePath)
			else -> throw IllegalArgumentException("Entity $entity does not support property access")
		}
}
