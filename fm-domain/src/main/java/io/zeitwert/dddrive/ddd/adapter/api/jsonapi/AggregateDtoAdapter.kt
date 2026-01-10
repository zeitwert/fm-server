package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

import dddrive.ddd.core.model.Aggregate

interface AggregateDtoAdapter<A : Aggregate, D : AggregateDto> {

	fun toAggregate(
		dto: D,
		aggregate: A,
	)

	fun fromAggregate(aggregate: A): D

}
