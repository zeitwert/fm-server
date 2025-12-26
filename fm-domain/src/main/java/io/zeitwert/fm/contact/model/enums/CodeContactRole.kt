package io.zeitwert.fm.contact.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

/** Contact role enum using the NEW dddrive framework. */
enum class CodeContactRole(
	override val defaultName: String,
) : EnumeratedEnum {

	COUNCILOR("Gemeinderat"),
	CARETAKER("Hauswart"),
	OTHER("Anderes"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeContactRole>(CodeContactRole::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getContactRole(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
