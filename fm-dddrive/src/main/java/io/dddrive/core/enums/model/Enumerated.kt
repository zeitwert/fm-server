package io.dddrive.core.enums.model

interface Enumerated {

	val enumeration: Enumeration<out Enumerated>

	val id: String

	fun getName(): String

}
