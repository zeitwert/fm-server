package io.zeitwert.fm.account.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeAccountType(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	PROSPECT("prospect", "Prospekt / Pilot"),
	CLIENT("client", "Kunde"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeAccountType>(CodeAccountType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getAccountType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
