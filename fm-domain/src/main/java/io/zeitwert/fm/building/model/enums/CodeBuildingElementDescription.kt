package io.zeitwert.fm.building.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeBuildingElementDescription(
	override val defaultName: String,
	val category: String,
) : EnumeratedEnum {

	;

	override val enumeration get() = Enumeration

	companion object Enumeration :
		EnumerationBase<CodeBuildingElementDescription>(CodeBuildingElementDescription::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		fun getElementDescription(itemId: String?) = if (itemId != null) getItem(itemId) else null

	}
}
