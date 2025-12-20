package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Gender enum using the NEW dddrive framework.
 */
enum class CodeGender(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	MALE("male", "Mann"),
	FEMALE("female", "Frau"),
	OTHER("other", "Andere"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeGender>(CodeGender::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getGender(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
