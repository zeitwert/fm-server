package io.zeitwert.persist.sql

import dddrive.ddd.model.Aggregate

interface SqlRecordMapper<A : Aggregate> {

	fun loadRecord(aggregate: A)

	fun storeRecord(aggregate: A)

}
