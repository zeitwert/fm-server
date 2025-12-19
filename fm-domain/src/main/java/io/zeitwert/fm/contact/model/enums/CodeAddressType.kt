package io.zeitwert.fm.contact.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

/**
 * Address type enum using the NEW dddrive framework.
 */
enum class CodeAddressType(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	MAIL("mail", "Mail Address"),
	EMAIL("email", "Email"),
	CHAT("chat", "Chat"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeAddressType>(CodeAddressType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getAddressType(itemId: String): CodeAddressType? = getItem(itemId)
	}
}
