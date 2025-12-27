package io.zeitwert.dddrive.persist

import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part

interface SqlIdProvider {

	fun nextAggregateId(): Any

	fun <P : Part<*>> nextPartId(
		aggregate: Aggregate,
		partClass: Class<P>,
	): Int

}
