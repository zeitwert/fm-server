package io.zeitwert.fm.contact.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

/** Salutation enum using the NEW dddrive framework. */
enum class CodeSalutation(
	override val defaultName: String,
	val genderId: String,
) : EnumeratedEnum {

	MR("Herr", "male"),
	MRS("Frau", "female"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeSalutation>(CodeSalutation::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getSalutation(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
