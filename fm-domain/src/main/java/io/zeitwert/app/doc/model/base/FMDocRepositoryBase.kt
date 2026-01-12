package io.zeitwert.app.doc.model.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocRepository
import dddrive.app.doc.model.base.DocRepositoryBase
import dddrive.query.QuerySpec
import io.zeitwert.persist.sql.ddd.AggregateSqlPersistenceProvider

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
	intfClass: Class<out Doc>,
	aggregateTypeId: String,
) : DocRepositoryBase<D>(intfClass, aggregateTypeId),
	DocRepository<D> {

}
