package dddrive.domain.household.persist.impl

import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.persist.ObjHouseholdPersistenceProvider
import dddrive.domain.obj.persist.base.MapObjPersistenceProviderBase
import org.springframework.stereotype.Component

/**
 * Map-based persistence provider for ObjHousehold.
 *
 * Automatically serializes/deserializes the aggregate using the property system.
 * No manual PTO mapping required.
 *
 * Active when persistence.type=map
 */
@Component("objHouseholdPersistenceProvider")
class MapObjHouseholdPersistenceProviderImpl :
	MapObjPersistenceProviderBase<ObjHousehold>(ObjHousehold::class.java),
	ObjHouseholdPersistenceProvider
