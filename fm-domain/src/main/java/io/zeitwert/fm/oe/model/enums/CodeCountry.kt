package io.zeitwert.fm.oe.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeCountry(
	override val defaultName: String,
) : EnumeratedEnum {

	CH("Schweiz"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeCountry>(CodeCountry::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getCountry(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
