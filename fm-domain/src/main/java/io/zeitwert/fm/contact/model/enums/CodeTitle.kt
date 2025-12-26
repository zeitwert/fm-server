package io.zeitwert.fm.contact.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

/** Title enum using the NEW dddrive framework. */
enum class CodeTitle(
	override val defaultName: String,
) : EnumeratedEnum {

	DR("Dr."),
	PROF("Prof."),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTitle>(CodeTitle::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getTitle(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
