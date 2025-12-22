package io.dddrive.domain.task.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeTaskPriority(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	LOW("low", "Low"),
	MEDIUM("medium", "Medium"),
	HIGH("high", "High"),
	URGENT("urgent", "Urgent"),
	;

	override val enumeration = Enumeration

	override fun getName() = itemName

	companion object Enumeration : EnumerationBase<CodeTaskPriority>(CodeTaskPriority::class.java) {
		init {
			entries.forEach { addItem(it) }
		}
	}
}
