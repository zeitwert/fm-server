package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Address channel enum using the NEW dddrive framework.
 */
enum class CodeAddressChannel(
	override val id: String,
	private val itemName: String,
	val addressTypeId: String,
) : Enumerated {

	MAIL("mail", "Mail Address", "mail"),
	EMAIL("email", "Email Address", "email"),
	WHATSAPP("whatsapp", "Whatsapp", "chat"),
	SIGNAL("signal", "Signal", "chat"),
	VIBER("viber", "Viber", "chat"),
	MESSENGER("messenger", "Messenger", "chat"),
	;

	override fun getName() = itemName

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
