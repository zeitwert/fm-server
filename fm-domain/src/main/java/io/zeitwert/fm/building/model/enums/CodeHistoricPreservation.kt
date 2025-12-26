package io.zeitwert.fm.building.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeHistoricPreservation(
	override val defaultName: String,
) : EnumeratedEnum {

	NONE("Kein Denkmalschutz"),
	PARTIAL("Teilweise geschützt"),
	FULL("Vollständig geschützt"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration :
		EnumerationBase<CodeHistoricPreservation>(CodeHistoricPreservation::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getHistoricPreservation(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
