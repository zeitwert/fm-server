package io.zeitwert.dddrive.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.app.model.RequestContextFM

interface AggregateSqlPersistenceProvider<A : Aggregate> : AggregatePersistenceProvider<A> {

	fun queryWithFilter(
		querySpec: QuerySpec?,
		requestCtx: RequestContextFM,
	): QuerySpec

	fun doFind(query: QuerySpec): List<Any>

}
