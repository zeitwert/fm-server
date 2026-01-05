package io.zeitwert.dddrive.persist

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.AggregatePersistenceProvider
import dddrive.ddd.query.QuerySpec

interface AggregateSqlPersistenceProvider<A : Aggregate> : AggregatePersistenceProvider<A> {

	fun find(query: QuerySpec?): List<Any>

	fun doFind(query: QuerySpec): List<Any>

}
