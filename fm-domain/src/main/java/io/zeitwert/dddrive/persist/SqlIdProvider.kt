package io.zeitwert.dddrive.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part

interface SqlIdProvider<A : Aggregate> {

	fun nextAggregateId(): Any

	fun <P : Part<A>> nextPartId(aggregate: A, partClass: Class<P>): Int

}
