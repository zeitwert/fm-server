package io.zeitwert.persist.sql.ddd

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part

interface SqlIdProvider {

	fun nextAggregateId(): Any

	fun <P : Part<*>> nextPartId(
		aggregate: Aggregate,
		partClass: Class<P>,
	): Int

}
