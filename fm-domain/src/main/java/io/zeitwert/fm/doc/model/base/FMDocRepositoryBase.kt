package io.zeitwert.fm.doc.model.base

import io.dddrive.doc.model.Doc
import io.dddrive.doc.model.DocRepository
import io.dddrive.doc.model.base.DocRepositoryBase

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
) : DocRepositoryBase<D>(repoIntfClass, intfClass, baseClass, aggregateTypeId)
