package io.zeitwert.fm.account.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeCurrency(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	CHF("chf", "CHF"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeCurrency>(CodeCurrency::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		fun getCurrency(itemId: String?): CodeCurrency? = if (itemId != null) getItem(itemId) else null
	}
}
