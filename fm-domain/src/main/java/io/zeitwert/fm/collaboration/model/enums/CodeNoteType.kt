package io.zeitwert.fm.collaboration.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase
import io.zeitwert.fm.building.model.enums.CodeBuildingType

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
		fun getNoteType(itemId: String): CodeNoteType? = getItem(itemId)
	}
}
