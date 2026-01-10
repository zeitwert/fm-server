package io.zeitwert.dddrive.api.jsonapi

import dddrive.ddd.model.Aggregate
import io.zeitwert.api.jsonapi.ResourceDto

interface AggregateDtoAdapter<A : Aggregate, D : ResourceDto> {

	fun toAggregate(
		dto: D,
		aggregate: A,
	)

	fun fromAggregate(aggregate: A): D

}
