package io.dddrive.domain.task.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeTaskPriority(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	LOW("low", "Low"),
	MEDIUM("medium", "Medium"),
	HIGH("high", "High"),
	URGENT("urgent", "Urgent"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTaskPriority>(CodeTaskPriority::class.java) {
		init {
			entries.forEach { addItem(it) }
		}
	}
}
