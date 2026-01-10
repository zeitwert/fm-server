package io.zeitwert.app.api.jsonapi

import dddrive.ddd.model.Aggregate

interface AggregateDtoAdapter<A : Aggregate, D : ResourceDto> {

	fun toAggregate(
		dto: D,
		aggregate: A,
	)

	fun fromAggregate(aggregate: A): D

}
