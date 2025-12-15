package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate
import org.jooq.UpdatableRecord

interface SqlAggregateRecordMapper<A : Aggregate, R : UpdatableRecord<R>> {

	fun nextId(): Any

	fun loadRecord(aggregateId: Any): R

	fun mapFromRecord(
		aggregate: A,
		record: R,
	)

	fun mapToRecord(aggregate: A): R

	fun storeRecord(
		record: R,
		aggregate: Aggregate,
	)

	fun getAll(tenantId: Any): List<Any>

	fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any>?

}
