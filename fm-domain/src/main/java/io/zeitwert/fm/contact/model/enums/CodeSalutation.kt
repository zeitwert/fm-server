package io.zeitwert.fm.contact.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

/**
 * Salutation enum using the NEW dddrive framework.
 */
enum class CodeSalutation(
	override val id: String,
	private val itemName: String,
	val genderId: String,
) : Enumerated {

	MR("mr", "Herr", "male"),
	MRS("mrs", "Frau", "female"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeSalutation>(CodeSalutation::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getSalutation(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
