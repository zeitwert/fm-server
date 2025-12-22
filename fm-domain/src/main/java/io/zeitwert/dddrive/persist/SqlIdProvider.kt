package io.zeitwert.dddrive.persist

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part

interface SqlIdProvider {

	fun nextAggregateId(): Any

	fun <P : Part<*>> nextPartId(
		aggregate: Aggregate,
		partClass: Class<P>,
	): Int

}
