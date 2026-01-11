package io.zeitwert.persist.sql.ddd

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec

interface AggregateSqlPersistenceProvider<A : Aggregate> : AggregatePersistenceProvider<A> {

	fun find(query: QuerySpec?): List<Any>

	fun doFind(query: QuerySpec): List<Any>

}
