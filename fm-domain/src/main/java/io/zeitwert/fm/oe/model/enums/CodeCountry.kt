package io.zeitwert.fm.oe.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeCountry(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	CH("ch", "Schweiz"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeCountry>(CodeCountry::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getCountry(itemId: String): CodeCountry? = getItem(itemId)
	}
}
