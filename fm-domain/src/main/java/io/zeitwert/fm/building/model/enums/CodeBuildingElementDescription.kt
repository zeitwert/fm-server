package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeBuildingElementDescription(
	override val id: String,
	private val itemName: String,
	val category: String,
) : Enumerated {

	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration :
		EnumerationBase<CodeBuildingElementDescription>(CodeBuildingElementDescription::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		fun getElementDescription(itemId: String): CodeBuildingElementDescription? = getItem(itemId)
	}
}
