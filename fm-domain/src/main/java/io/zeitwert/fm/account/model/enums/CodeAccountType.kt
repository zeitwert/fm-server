package io.zeitwert.fm.account.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeAccountType(
	override val defaultName: String,
) : EnumeratedEnum {

	PROSPECT("Prospekt / Pilot"),
	CLIENT("Kunde"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeAccountType>(CodeAccountType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getAccountType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
