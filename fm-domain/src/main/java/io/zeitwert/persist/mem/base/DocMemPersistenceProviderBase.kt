package io.zeitwert.persist.mem.base

import dddrive.app.doc.model.Doc
import dddrive.db.MemoryDb
import dddrive.query.QuerySpec

/**
 * Base class for memory-based persistence providers for Doc aggregates.
 *
 * Extends AggregateMemPersistenceProviderBase and adds Doc-specific query tightening:
 * - Applies tenant and account filters via queryWithFilter
 */
abstract class DocMemPersistenceProviderBase<D : Doc>(
	intfClass: Class<D>,
) : AggregateMemPersistenceProviderBase<D>(intfClass) {

	override val hasAccount = true

	override fun find(query: QuerySpec?): List<Any> {
		val querySpec = queryWithFilter(query)
		return MemoryDb
			.find(intfClass, querySpec)
			.mapNotNull { map -> map["id"] as? Int }
	}

}
