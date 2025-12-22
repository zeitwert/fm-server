package io.dddrive.enums.model

interface Enumeration<E : Enumerated> {

	val area: String

	val module: String

	val id: String

	val items: List<E>

	fun getItem(id: String): E

	val resourcePath: String

}
