package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

import dddrive.ddd.core.model.Aggregate

interface GenericAggregateDto<A : Aggregate> :
	AggregateDto<A>,
	GenericDto {

	val meta: Map<String, Any?>

	fun hasRelation(name: String): Boolean

	fun setRelation(
		name: String,
		value: Any?,
	)

	fun getRelation(name: String): Any?

}
