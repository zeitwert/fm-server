package dddrive.domain.task.model.enums

import dddrive.ddd.model.EnumeratedEnum
import dddrive.ddd.model.base.EnumerationBase

enum class CodeTaskPriority(
	override val defaultName: String,
) : EnumeratedEnum {

	LOW("Low"),
	MEDIUM("Medium"),
	HIGH("High"),
	URGENT("Urgent"),
	;

	override val enumeration = Enumeration

	companion object Enumeration : EnumerationBase<CodeTaskPriority>(CodeTaskPriority::class.java) {
		init {
			entries.forEach { addItem(it) }
		}
	}
}
