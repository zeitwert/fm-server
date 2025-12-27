package io.zeitwert.fm.building.model.enums

import dddrive.ddd.enums.model.Enumerated
import dddrive.ddd.enums.model.base.EnumerationBase

enum class CodeBuildingMaintenanceStrategy(
	override val defaultName: String,
) : Enumerated {

	M("Minimal"),
	N("Normal"),
	NW("Normal Wohlen"),
	;

	override val enumeration get() = Enumeration

	override val id = name

	companion object Enumeration :
		EnumerationBase<CodeBuildingMaintenanceStrategy>(CodeBuildingMaintenanceStrategy::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		fun getMaintenanceStrategy(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
