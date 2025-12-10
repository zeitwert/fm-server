package io.dddrive.core.property.path.handlers

import io.dddrive.core.property.model.EntityWithProperties
import io.dddrive.core.property.model.Property
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.core.property.path.PathElementHandler
import io.dddrive.core.property.path.PathHandlingContext
import io.dddrive.core.property.path.PathHandlingResult
import org.springframework.stereotype.Component

/**
 * Handles reference property ID access via "Id" suffix for automatic reference ID access.
 *
 * Converts paths like "pillarTwoStatementId" to "pillarTwoStatement.id" automatically,
 * allowing direct access to reference property IDs without requiring manual property definitions.
 *
 * This handler:
 * - Detects paths ending with "Id" (capital I)
 * - Finds the corresponding reference property by removing the "Id" suffix
 * - Delegates to the reference property's .id access
 *
 * Only handles simple property names (e.g., "pillarTwoStatementId") and delegates
 * complex paths to other handlers first (e.g., "partnerList[0].pillarTwoStatementId"
 * is handled by list handler first).
 */
@Component
class ReferencePropertyIdHandler : PathElementHandler {
	private companion object {
		private const val ID_SUFFIX = "Id"
	}

	override fun canHandle(
		path: String,
		property: Property<*>?,
		entity: Any,
	): Boolean {
		if (!path.endsWith(ID_SUFFIX)) return false
		
		// Only handle simple property names, not complex paths
		if (path.contains('.')) return false
		
		val basePath = path.removeSuffix(ID_SUFFIX)
		if (basePath.isEmpty()) return false
		
		return try {
			val baseProperty = getBaseProperty(basePath, entity)
			baseProperty is ReferenceProperty<*>
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

		if (baseProperty !is ReferenceProperty<*>) {
			throw IllegalArgumentException("Property at path '$basePath' is not a ReferenceProperty, required for Id handling.")
		}

		// Set the ID directly on the reference property
		baseProperty.id = value
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

		if (baseProperty !is ReferenceProperty<*>) {
			return PathHandlingResult.complete(null)
		}

		// Get the ID directly from the reference property
		return PathHandlingResult.complete(baseProperty.id)
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
