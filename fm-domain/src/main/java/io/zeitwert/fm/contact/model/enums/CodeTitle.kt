package io.zeitwert.fm.contact.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

/**
 * Title enum using the NEW dddrive framework.
 */
enum class CodeTitle(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	DR("dr", "Dr."),
	PROF("prof", "Prof."),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTitle>(CodeTitle::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getTitle(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
