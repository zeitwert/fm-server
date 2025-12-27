package io.zeitwert.dddrive.ddd.api.rest

import dddrive.ddd.core.model.Aggregate
import io.zeitwert.dddrive.ddd.api.rest.dto.AggregateDto

interface AggregateDtoAdapter<A : Aggregate, D : AggregateDto<A>> {

	fun toAggregate(
		dto: D,
		aggregate: A,
	)

	fun fromAggregate(
		aggregate: A,
		detailLevel: DtoDetailLevel,
	): D

	fun fromAggregate(
		id: String,
		detailLevel: DtoDetailLevel,
	): D
}
