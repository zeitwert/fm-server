package io.zeitwert.fm.doc.model.base

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocRepository
import io.dddrive.doc.model.base.DocRepositoryBase
import io.zeitwert.dddrive.persist.AggregateSqlPersistenceProvider
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.doc.model.FMDocRepository

/**
 * Base repository class for FM Orders.
 *
 * This class extends the new dddrive DocRepositoryBase and adds:
 * - DSLContext injection for jOOQ operations
 *
 * Subclasses must implement getPersistenceProvider() to return their specific provider.
 *
 * @param D The Doc entity type
 */
abstract class FMDocRepositoryBase<D : Doc>(
	repoIntfClass: Class<out DocRepository<D>>,
	intfClass: Class<out Doc>,
	baseClass: Class<out Doc>,
	aggregateTypeId: String,
) : DocRepositoryBase<D>(repoIntfClass, intfClass, baseClass, aggregateTypeId),
	FMDocRepository<D> {

	override val persistenceProvider get() = super.persistenceProvider as AggregateSqlPersistenceProvider<D>

	override fun find(
		query: QuerySpec?,
		requestContext: RequestContextFM,
	): List<Any> = persistenceProvider.doFind(query, requestContext)

}
