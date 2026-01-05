package io.zeitwert.dddrive.persist

import dddrive.ddd.core.model.Aggregate

interface SqlRecordMapper<A : Aggregate> {

	fun loadRecord(aggregate: A)

	fun storeRecord(aggregate: A)

}
