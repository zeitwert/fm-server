package dddrive.domain.obj.persist.base

import dddrive.app.obj.model.Obj
import dddrive.domain.ddd.persist.map.base.MapAggregatePersistenceProviderBase

/**
 * Base class for map-based Obj persistence providers.
 *
 * Adds objTypeId to the serialized map for foreign key lookups.
 */
abstract class MapObjPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : MapAggregatePersistenceProviderBase<O>(intfClass) {

	companion object {

		// Properties that are stored for foreign key lookups but are computed, not settable
		private val COMPUTED_PROPERTIES = setOf("objTypeId")
	}

	override fun fromAggregate(aggregate: O): Map<String, Any?> {
		val baseMap = super.fromAggregate(aggregate).toMutableMap()
		// Add objTypeId for foreign key lookups - it's a computed property not a BaseProperty
		baseMap["objTypeId"] = aggregate.meta.objTypeId
		return baseMap
	}

	override fun toAggregate(
		map: Map<String, Any?>,
		aggregate: O,
	) {
		// Filter out computed properties before deserializing
		val filteredMap = map.filterKeys { it !in COMPUTED_PROPERTIES }
		super.toAggregate(filteredMap, aggregate)
	}

}
