package io.zeitwert.dddrive.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.AggregatePersistenceProvider

interface AggregateSqlPersistenceProvider<A : Aggregate> : AggregatePersistenceProvider<A> {

	fun find(query: QuerySpec?): List<Any>

	fun doFind(query: QuerySpec): List<Any>

}
