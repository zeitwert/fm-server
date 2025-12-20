package io.zeitwert.fm.obj.model.base

import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjRepositoryBase

/**
 * Base repository class for FM Objects.
 *
 * This class extends the new dddrive ObjRepositoryBase and adds:
 * - DSLContext injection for jOOQ operations
 *
 * Subclasses must implement getPersistenceProvider() to return their specific provider.
 *
 * @param O The Obj entity type
 */
abstract class FMObjRepositoryBase<O : Obj>(
	repoIntfClass: Class<out ObjRepository<O>>,
	intfClass: Class<out Obj>,
	baseClass: Class<out Obj>,
	aggregateTypeId: String,
) : ObjRepositoryBase<O>(repoIntfClass, intfClass, baseClass, aggregateTypeId)
