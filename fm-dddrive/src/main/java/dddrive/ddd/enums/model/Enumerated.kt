package dddrive.ddd.enums.model

/**
 * An interface for enumerated types.
 */
interface Enumerated {

	val enumeration: Enumeration<out Enumerated>

	val id: String

	val defaultName: String

}
