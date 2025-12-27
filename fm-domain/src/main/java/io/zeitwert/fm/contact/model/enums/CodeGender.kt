package io.zeitwert.fm.contact.model.enums

import dddrive.ddd.enums.model.EnumeratedEnum
import dddrive.ddd.enums.model.base.EnumerationBase

/** Gender enum using the NEW dddrive framework. */
enum class CodeGender(
	override val defaultName: String,
) : EnumeratedEnum {

	MALE("Mann"),
	FEMALE("Frau"),
	OTHER("Andere"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeGender>(CodeGender::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getGender(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
