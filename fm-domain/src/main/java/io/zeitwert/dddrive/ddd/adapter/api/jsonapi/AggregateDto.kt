package io.zeitwert.dddrive.ddd.adapter.api.jsonapi

interface AggregateDto : JsonDto {

	val meta: Map<String, Any?>?

	fun hasRelation(name: String): Boolean

	fun setRelation(
		name: String,
		value: Any?,
	)

	fun getRelation(name: String): Any?

	fun hasOperation(name: String): Boolean

}
