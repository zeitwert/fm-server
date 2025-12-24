package io.zeitwert.fm.obj.model.base

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjRepository
import io.dddrive.obj.model.base.ObjRepositoryBase
import io.zeitwert.dddrive.persist.AggregateSqlPersistenceProvider
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.obj.model.FMObjRepository

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
) : ObjRepositoryBase<O>(repoIntfClass, intfClass, baseClass, aggregateTypeId),
	FMObjRepository<O> {

	override val persistenceProvider get() = super.persistenceProvider as AggregateSqlPersistenceProvider<O>

	override fun find(
		query: QuerySpec?,
		requestContext: RequestContextFM,
	): List<Any> = persistenceProvider.doFind(query, requestContext)

}
