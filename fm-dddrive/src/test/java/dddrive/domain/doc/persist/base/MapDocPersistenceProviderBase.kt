package dddrive.domain.doc.persist.base

import dddrive.app.doc.model.Doc
import dddrive.domain.ddd.persist.map.base.MapAggregatePersistenceProviderBase

/**
 * Base class for map-based Doc persistence providers.
 *
 * Adds docTypeId to the serialized map for foreign key lookups.
 */
abstract class MapDocPersistenceProviderBase<D : Doc>(
	intfClass: Class<D>,
) : MapAggregatePersistenceProviderBase<D>(intfClass) {

	companion object {
		// Properties that are stored for foreign key lookups but are computed, not settable
		private val COMPUTED_PROPERTIES = setOf("docTypeId")
	}

	override fun fromAggregate(aggregate: D): Map<String, Any?> {
		val baseMap = super.fromAggregate(aggregate).toMutableMap()
		// Add docTypeId for foreign key lookups - it's a computed property not a BaseProperty
		baseMap["docTypeId"] = aggregate.meta.docTypeId
		return baseMap
	}

	override fun toAggregate(
		map: Map<String, Any?>,
		aggregate: D,
	) {
		// Filter out computed properties before deserializing
		val filteredMap = map.filterKeys { it !in COMPUTED_PROPERTIES }
		super.toAggregate(filteredMap, aggregate)
	}

}
