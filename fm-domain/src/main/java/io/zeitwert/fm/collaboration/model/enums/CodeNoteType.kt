package io.zeitwert.fm.collaboration.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

/**
 * Note type enum using the NEW dddrive framework.
 */
enum class CodeNoteType(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	NOTE("note", "Note"),
	CALL("call", "Call"),
	VISIT("visit", "Visit"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeNoteType>(CodeNoteType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getNoteType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
