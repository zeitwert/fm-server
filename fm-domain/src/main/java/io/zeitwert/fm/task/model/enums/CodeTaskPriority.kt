package io.zeitwert.fm.task.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeTaskPriority(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	LOW("low", "Tief"),
	NORMAL("normal", "Normal"),
	HIGH("high", "Hoch"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTaskPriority>(CodeTaskPriority::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		@JvmStatic
		fun getPriority(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
