package dddrive.domain.obj.persist.base

import dddrive.app.obj.model.Obj
import dddrive.domain.ddd.persist.map.base.MemAggregatePersistenceProviderBase

/**
 * Base class for map-based Obj persistence providers.
 *
 * Adds objTypeId to the serialized map for foreign key lookups.
 */
abstract class MemObjPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : MemAggregatePersistenceProviderBase<O>(intfClass)
