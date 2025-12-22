package io.zeitwert.dddrive.ddd.api.rest

import io.zeitwert.dddrive.ddd.api.rest.dto.AggregateDto
import io.dddrive.ddd.model.Aggregate

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
