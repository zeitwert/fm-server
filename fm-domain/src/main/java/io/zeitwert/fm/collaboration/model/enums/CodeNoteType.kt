package io.zeitwert.fm.collaboration.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Note type enum using the NEW dddrive framework.
 */
enum class CodeNoteType(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	NOTE("note", "Note"),
	CALL("call", "Call"),
	VISIT("visit", "Visit"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeNoteType>(CodeNoteType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getNoteType(itemId: String): CodeNoteType? = getItem(itemId)
	}
}
