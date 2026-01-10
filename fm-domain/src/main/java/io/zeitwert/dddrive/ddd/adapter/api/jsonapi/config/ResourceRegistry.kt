package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.config

import dddrive.app.ddd.model.Aggregate
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoAdapterBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Entry in the resource registry containing all metadata for a JSON API resource.
 *
 * @param aggregateClass The aggregate class this resource represents
 * @param resourceType The JSON API resource type (e.g., "account", "contact")
 * @param dtoClass The DTO class for this resource
 * @param adapter The adapter that handles conversion between aggregate and DTO
 */
data class ResourceEntry(
	val aggregateClass: Class<out Aggregate>,
	val resourceType: String,
	val dtoClass: Class<out AggregateDtoBase>,
	val adapter: AggregateDtoAdapterBase<*, *>,
) {

	val relationships: Collection<RelationshipConfig>
		get() = adapter.config.relationshipConfigs

}

/**
 * Central registry for JSON API resources.
 *
 * Adapters self-register during initialization, providing the registry with all
 * metadata needed for dynamic relationship field contribution.
 *
 * This enables:
 * - Dynamic relationship field generation via ResourceFieldContributor
 * - Lookup of resource type from aggregate class (for relationship targets)
 * - Central source of truth for all resource mappings
 */
object ResourceRegistry {

	val logger: Logger = LoggerFactory.getLogger(ResourceRegistry::class.java)!!

	private val entries = mutableListOf<ResourceEntry>()
	private val byResourceType = mutableMapOf<String, ResourceEntry>()
	private val byAggregateClass = mutableMapOf<Class<*>, ResourceEntry>()

	/**
	 * Register a resource entry.
	 * Called by adapters during their initialization.
	 */
	fun register(entry: ResourceEntry) {
		logger.debug("RR[${entry.resourceType}, ${entry.aggregateClass.simpleName}, ${entry.dtoClass.simpleName}]")
		entries.add(entry)
		byResourceType[entry.resourceType] = entry
		byAggregateClass[entry.aggregateClass] = entry
	}

	/**
	 * Get all registered entries.
	 */
	fun getEntries(): List<ResourceEntry> = entries.toList()

	/**
	 * Find entry by JSON API resource type.
	 */
	fun byResourceType(type: String): ResourceEntry? = byResourceType[type]

	/**
	 * Find entry by aggregate class.
	 */
	fun byAggregateClass(clazz: Class<*>): ResourceEntry? = byAggregateClass[clazz]

	/**
	 * Clear all entries (useful for testing).
	 */
	fun clear() {
		entries.clear()
		byResourceType.clear()
		byAggregateClass.clear()
	}

}
