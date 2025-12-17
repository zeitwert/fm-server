package io.dddrive.core.property.model.impl

import io.dddrive.core.ddd.model.Aggregate

fun interface AggregateResolver<A : Aggregate> {

	fun get(id: Any): A

}
