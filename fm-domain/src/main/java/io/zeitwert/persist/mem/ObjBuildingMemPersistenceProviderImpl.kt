package io.zeitwert.persist.mem

import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.persist.ObjBuildingPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjBuilding.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objBuildingPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjBuildingMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjBuilding>(ObjBuilding::class.java),
	ObjBuildingPersistenceProvider
