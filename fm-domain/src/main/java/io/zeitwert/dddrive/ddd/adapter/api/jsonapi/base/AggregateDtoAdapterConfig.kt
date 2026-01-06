package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base

import dddrive.ddd.core.model.Part
import dddrive.ddd.property.model.AggregateReferenceProperty
import dddrive.ddd.property.model.AggregateReferenceSetProperty
import dddrive.ddd.property.model.EntityWithProperties
import dddrive.ddd.property.model.Property
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.JsonApiDto

/**
 * Configuration for a relationship to be registered with the adapter.
 *
 * @param sourceProperty The name of the property on the aggregate (e.g., "mainContact",
 *   "logoImage")
 * @param dataSource Function to obtain the related ID(s) from the aggregate and DTO (either this or
 *   aggregatePropertyName must be provided)
 * @param targetRelation The name of the ID field on the DTO relation (e.g., "mainContactId",
 *   "logoId")
 * @param resourceType The JSON API resource type of the target (e.g., "contact", "document")
 */
data class RelationshipConfig(
	val sourceProperty: String?,
	val dataSource: ((EntityWithProperties, JsonApiDto) -> Any?)?,
	val targetRelation: String,
	val resourceType: String,
)

/**
 * Configuration for a field mapping to be registered with the adapter.
 *
 * @param targetField The name of the field on the DTO (e.g., "tenants")
 * @param sourceProperty The name of the property on the aggregate (e.g., "tenantSet"), or null if
 *   using custom functions
 * @param outgoing Function to compute the DTO value from the entity (for fromAggregate)
 * @param incoming Function to apply the DTO value to the entity (for toAggregate)
 */
data class FieldConfig(
	val targetField: String,
	val sourceProperty: String?,
	val outgoing: ((EntityWithProperties) -> Any?)?,
	val incoming: ((Any?, EntityWithProperties) -> Unit)?,
)

/**
 * Configuration for customizing part serialization/deserialization.
 *
 * Parts without explicit configuration are serialized using the generic infrastructure. This allows
 * adding computed fields, excluding properties, or transforming values for specific part types.
 *
 * @param P The part type
 */
class PartAdapterConfig<P : Part<*>> {

	internal val exclusions = mutableListOf<String>()
	internal val fields = mutableListOf<FieldConfig>()

	/** Exclude a property from automatic serialization. */
	fun exclude(propertyName: String) {
		exclusions.add(propertyName)
	}

	fun exclude(propertyNames: List<String>) {
		exclusions.addAll(propertyNames)
	}

	/** Register a field mapping from a source property to a target field. */
	fun field(
		targetField: String,
		sourceProperty: String? = null,
	) = fields.add(FieldConfig(targetField, sourceProperty ?: targetField, null, null))

	/**
	 * Register a field with custom outgoing and incoming functions.
	 *
	 * @param targetField The name of the field on the DTO
	 * @param outgoing Function to compute the DTO value from the part (for serialization)
	 * @param incoming Function to apply the DTO value to the part (for deserialization)
	 */
	fun field(
		targetField: String,
		outgoing: (EntityWithProperties) -> Any?,
		incoming: ((Any?, EntityWithProperties) -> Unit)? = null,
	) {
		fields.add(FieldConfig(targetField, null, outgoing, incoming))
	}

	/** Check if a property should be excluded from automatic serialization. */
	fun isExcluded(property: Property<*>): Boolean =
		property is AggregateReferenceProperty ||
			property is AggregateReferenceSetProperty ||
			property.name in exclusions ||
			fields.any { property.name == it.sourceProperty }

}

/**
 * Configuration for customizing aggregate serialization/deserialization.
 *
 * This class holds all configuration for an adapter:
 * - Property exclusions
 * - Relationship mappings
 * - Field mappings
 * - Meta field mappings
 * - Part adapter configurations
 *
 * Configuration is built via a fluent DSL and then used by the adapter for conversion.
 */
class AggregateDtoAdapterConfig {

	internal val exclusions = mutableListOf<String>()
	internal val relationships = mutableListOf<RelationshipConfig>()
	internal val fields = mutableListOf<FieldConfig>()
	internal val metas = mutableListOf<FieldConfig>()
	internal val partAdapters = mutableMapOf<Class<*>, PartAdapterConfig<*>>()

	/**
	 * Exclude a property from automatic serialization.
	 *
	 * @param propertyName The name of the property to exclude
	 */
	fun exclude(propertyName: String) = exclusions.add(propertyName)

	fun exclude(propertyNames: List<String>) = exclusions.addAll(propertyNames)

	/**
	 * Register a single-value relationship.
	 *
	 * @param targetRelation The name of the ID field on the DTO (e.g., "mainContactId", "logoId")
	 * @param resourceType The JSON API resource type of the target (e.g., "contact", "document")
	 * @param sourceProperty The name of the aggregate property (e.g., "mainContact", "logoImage")
	 */
	fun relationship(
		targetRelation: String,
		resourceType: String,
		sourceProperty: String? = null,
	) = addRelationship(targetRelation, resourceType, sourceProperty ?: targetRelation, null)

	/**
	 * Register a single-value relationship.
	 *
	 * @param targetRelation The name of the IDs field on the DTO (e.g., "mainContactId", "logoId")
	 * @param resourceType The JSON API resource type of the target (e.g., "contact", "document")
	 * @param dataSource Function to obtain the related ID from the aggregate
	 */
	fun relationship(
		targetRelation: String,
		resourceType: String,
		dataSource: (EntityWithProperties, JsonApiDto) -> Any?,
	) = addRelationship(targetRelation, resourceType, null, dataSource)

	private fun addRelationship(
		targetRelation: String,
		resourceType: String,
		sourceProperty: String?,
		dataSource: ((EntityWithProperties, JsonApiDto) -> Any?)?,
	) = relationships.add(
		RelationshipConfig(
			sourceProperty,
			dataSource,
			targetRelation,
			resourceType,
		),
	)

	/**
	 * Register a field mapping from a source property to a target field.
	 *
	 * Uses intelligent type detection:
	 * - ReferenceSetProperty -> List<EnumeratedDto> (loads each entity)
	 * - AggregateReferenceProperty -> EnumeratedDto (loads entity)
	 * - Other properties -> direct value copy
	 *
	 * @param targetField The name of the field on the DTO (e.g., "tenants")
	 * @param sourceProperty The name of the property on the aggregate (e.g., "tenantSet")
	 */
	fun field(
		targetField: String,
		sourceProperty: String? = null,
	) = fields.add(FieldConfig(targetField, sourceProperty ?: targetField, null, null))

	/**
	 * Register a field with custom outgoing and incoming functions.
	 *
	 * @param targetField The name of the field on the DTO
	 * @param outgoing Function to compute the DTO value from the entity (for fromAggregate)
	 * @param incoming Function to apply the DTO value to the entity (for toAggregate)
	 */
	fun field(
		targetField: String,
		outgoing: (EntityWithProperties) -> Any?,
		incoming: ((Any?, EntityWithProperties) -> Unit)? = null,
	) = fields.add(FieldConfig(targetField, null, outgoing, incoming))

	fun meta(
		targetField: String,
		sourceProperty: String? = null,
	) = metas.add(FieldConfig(targetField, sourceProperty ?: targetField, null, null))

	fun meta(properties: List<String>) = metas.addAll(properties.map { FieldConfig(it, it, null, null) })

	fun meta(
		targetField: String,
		outgoing: (EntityWithProperties) -> Any?,
		incoming: ((Any?, EntityWithProperties) -> Unit)? = null,
	) = metas.add(FieldConfig(targetField, null, outgoing, incoming))

	/** Check if a property should be excluded from automatic serialization. */
	fun isExcluded(property: Property<*>): Boolean =
		property is AggregateReferenceProperty ||
			property is AggregateReferenceSetProperty ||
			property.name in exclusions ||
			relationships.any { property.name == it.sourceProperty } ||
			fields.any { property.name == it.sourceProperty } ||
			metas.any { property.name == it.sourceProperty }

	/**
	 * Register a part adapter configuration for customizing part serialization.
	 *
	 * Parts without explicit configuration are serialized using the generic infrastructure.
	 *
	 * @param partClass The part class to configure
	 * @param configure Configuration block to set up exclusions and custom fields
	 */
	fun <P : Part<*>> partAdapter(
		partClass: Class<P>,
		configure: PartAdapterConfig<P>.() -> Unit,
	) {
		val config = PartAdapterConfig<P>()
		config.configure()
		partAdapters[partClass] = config
	}

	/**
	 * Find a part adapter config by searching the class hierarchy (interfaces and superclasses). This
	 * allows registering adapters using interface types while matching implementation classes at
	 * runtime.
	 */
	internal fun partAdapterConfig(partClass: Class<*>): PartAdapterConfig<*> {
		// Direct match first
		partAdapters[partClass]?.let {
			return it
		}
		// Check interfaces
		for (iface in partClass.interfaces) {
			partAdapters[iface]?.let {
				return it
			}
		}
		// Check superclass hierarchy
		var superclass = partClass.superclass
		while (superclass != null) {
			partAdapters[superclass]?.let {
				return it
			}
			for (iface in superclass.interfaces) {
				partAdapters[iface]?.let {
					return it
				}
			}
			superclass = superclass.superclass
		}
		// Register empty config and return
		partAdapters[partClass] = PartAdapterConfig<Part<*>>()
		return partAdapters[partClass]!!
	}

}
