package io.zeitwert.fm.obj.model.base

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.ddd.query.QuerySpec
import io.zeitwert.dddrive.persist.AggregateSqlPersistenceProvider

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
	intfClass: Class<out Obj>,
	aggregateTypeId: String,
) : ObjRepositoryBase<O>(intfClass, aggregateTypeId),
	ObjRepository<O> {

	override val persistenceProvider get() = super.persistenceProvider as AggregateSqlPersistenceProvider<O>

	override fun find(query: QuerySpec?): List<Any> = persistenceProvider.find(query)

}
