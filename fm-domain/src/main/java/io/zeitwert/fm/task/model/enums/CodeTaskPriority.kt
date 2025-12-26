package io.zeitwert.fm.task.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeTaskPriority(
	override val defaultName: String,
) : EnumeratedEnum {

	LOW("Tief"),
	NORMAL("Normal"),
	HIGH("Hoch"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTaskPriority>(CodeTaskPriority::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getPriority(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
