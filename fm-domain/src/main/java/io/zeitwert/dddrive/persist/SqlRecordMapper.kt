package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate
import org.jooq.UpdatableRecord

interface SqlRecordLoader<A : Aggregate, R : UpdatableRecord<R>> {

	fun loadRecord(aggregate: A): R

	fun mapFromRecord(
		aggregate: A,
		record: R,
	)

	fun mapToRecord(aggregate: A): R

	fun storeRecord(
		record: R,
		aggregate: A,
	)

	fun getAll(tenantId: Any): List<Any>

	fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>?

}
