package io.zeitwert.fm.building.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeBuildingRatingStatus(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	OPEN("open", "Open"),
	REVIEW("review", "Review"),
	DONE("done", "Done"),
	DISCARD("discard", "Discarded"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeBuildingRatingStatus>(CodeBuildingRatingStatus::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getRatingStatus(itemId: String): CodeBuildingRatingStatus? = getItem(itemId)
	}
}
