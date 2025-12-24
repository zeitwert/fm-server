package io.zeitwert.dddrive.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.app.model.RequestContextFM

interface AggregateSqlPersistenceProvider<A : Aggregate> : AggregatePersistenceProvider<A> {

	fun doFind(
		query: QuerySpec?,
		requestCtx: RequestContextFM,
	): List<Any>

	fun doFind(query: QuerySpec): List<Any>

}
