package io.zeitwert.fm.task.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

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
		fun getPriority(itemId: String): CodeTaskPriority? = getItem(itemId)
	}
}
