package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Contact role enum using the NEW dddrive framework.
 */
enum class CodeContactRole(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	COUNCILOR("councilor", "Gemeinderat"),
	CARETAKER("caretaker", "Hauswart"),
	OTHER("other", "Anderes"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeContactRole>(CodeContactRole::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContactRole(itemId: String): CodeContactRole? = getItem(itemId)
	}
}
