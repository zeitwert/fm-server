package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Title enum using the NEW dddrive framework.
 */
enum class CodeTitle(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	DR("dr", "Dr."),
	PROF("prof", "Prof."),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTitle>(CodeTitle::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getTitle(itemId: String): CodeTitle? = getItem(itemId)
	}
}
