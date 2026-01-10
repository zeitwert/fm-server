package dddrive.ddd.model

/**
 * An interface for enumerated types that use their name as the identifier (which maps nicely to kotlin enum classes).
 */
interface EnumeratedEnum : Enumerated {

	val name: String

	override val id get() = name.lowercase()

}
