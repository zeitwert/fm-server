package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Address channel enum using the NEW dddrive framework.
 */
enum class CodeAddressChannel(
	private val id: String,
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

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	val addressType: CodeAddressType?
		get() = CodeAddressType.getAddressType(addressTypeId)

	val isMailAddress: Boolean
		get() = addressTypeId == "mail"

	companion object Enumeration : EnumerationBase<CodeAddressChannel>(CodeAddressChannel::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getAddressChannel(itemId: String): CodeAddressChannel? = getItem(itemId)
	}
}
