package io.zeitwert.api.jsonapi

interface ResourceDto : AttributeDto {

	val meta: Map<String, Any?>?

	override val id: String?

	override fun hasAttribute(key: String): Boolean

	override fun setAttribute(
		key: String,
		value: Any?,
	)

	override fun getAttribute(key: String): Any?

	fun hasRelation(name: String): Boolean

	fun setRelation(
		name: String,
		value: Any?,
	)

	fun getRelation(name: String): Any?

	fun hasOperation(name: String): Boolean

}
