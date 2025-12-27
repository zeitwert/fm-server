package io.zeitwert.fm.contact.model.enums

import dddrive.ddd.enums.model.EnumeratedEnum
import dddrive.ddd.enums.model.base.EnumerationBase

/** Address channel enum using the NEW dddrive framework. */
enum class CodeAddressChannel(
	override val defaultName: String,
	val addressTypeId: String,
) : EnumeratedEnum {

	MAIL("Mail Address", "mail"),
	EMAIL("Email Address", "email"),
	WHATSAPP("Whatsapp", "chat"),
	SIGNAL("Signal", "chat"),
	VIBER("Viber", "chat"),
	MESSENGER("Messenger", "chat"),
	;

	override val enumeration get() = Enumeration

	val addressType get() = CodeAddressType.getAddressType(addressTypeId)

	val isMailAddress get() = addressTypeId == "mail"

	companion object Enumeration : EnumerationBase<CodeAddressChannel>(CodeAddressChannel::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getAddressChannel(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
