package io.zeitwert.fm.contact.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

/**
 * Contact role enum using the NEW dddrive framework.
 */
enum class CodeContactRole(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	COUNCILOR("councilor", "Gemeinderat"),
	CARETAKER("caretaker", "Hauswart"),
	OTHER("other", "Anderes"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeContactRole>(CodeContactRole::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContactRole(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
