package io.zeitwert.fm.contact.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

/** Address type enum using the NEW dddrive framework. */
enum class CodeAddressType(
	override val defaultName: String,
) : EnumeratedEnum {

	MAIL("Mail Address"),
	EMAIL("Email"),
	CHAT("Chat"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeAddressType>(CodeAddressType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getAddressType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
