package io.zeitwert.fm.building.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeBuildingRatingStatus(
	override val defaultName: String,
) : EnumeratedEnum {

	OPEN("Open"),
	REVIEW("Review"),
	DONE("Done"),
	DISCARD("Discarded"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration :
		EnumerationBase<CodeBuildingRatingStatus>(CodeBuildingRatingStatus::class.java) {

		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getRatingStatus(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
