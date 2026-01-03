package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

import dddrive.ddd.core.model.Aggregate
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto

interface GenericAggregateDto<A : Aggregate> :
	AggregateDto<A>,
	GenericDto {

	var meta: AggregateMetaDto?

	fun hasRelation(name: String): Boolean

	fun setRelation(
		name: String,
		value: Any?,
	)

	fun getRelation(name: String): Any?

}
