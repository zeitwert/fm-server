package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeBuildingMaintenanceStrategy(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	M("M", "Minimal"),
	N("N", "Normal"),
	NW("NW", "Normal Wohlen"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration :
		EnumerationBase<CodeBuildingMaintenanceStrategy>(CodeBuildingMaintenanceStrategy::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		fun getMaintenanceStrategy(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
