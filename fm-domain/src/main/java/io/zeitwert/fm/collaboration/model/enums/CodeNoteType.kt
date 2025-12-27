package io.zeitwert.fm.collaboration.model.enums

import dddrive.ddd.enums.model.EnumeratedEnum
import dddrive.ddd.enums.model.base.EnumerationBase

/** Note type enum using the NEW dddrive framework. */
enum class CodeNoteType(
	override val defaultName: String,
) : EnumeratedEnum {

	NOTE("Note"),
	CALL("Call"),
	VISIT("Visit"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeNoteType>(CodeNoteType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getNoteType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
