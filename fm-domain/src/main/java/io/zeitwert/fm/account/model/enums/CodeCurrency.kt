package io.zeitwert.fm.account.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeCurrency(
	override val defaultName: String,
) : EnumeratedEnum {

	CHF("CHF"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeCurrency>(CodeCurrency::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		fun getCurrency(itemId: String?): CodeCurrency? = if (itemId != null) getItem(itemId) else null
	}
}
