package io.zeitwert.fm.oe.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeLocale(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	EN_US("en-US", "English US"),
	EN_UK("en-UK", "English UK"),
	DE_CH("de-CH", "German CH"),
	DE_DE("de-DE", "German DE"),
	FR_CH("fr-CH", "French CH"),
	FR_FR("fr-FR", "French FR"),
	ES_ES("es-ES", "Spanish ES"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeLocale>(CodeLocale::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getLocale(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
