package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate

interface SqlRecordMapper<A : Aggregate> {

	fun loadRecord(aggregate: A)

	fun storeRecord(aggregate: A)

	fun getAll(tenantId: Any): List<Any>

	fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>?

}
