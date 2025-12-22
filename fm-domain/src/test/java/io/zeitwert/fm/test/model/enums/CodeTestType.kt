package io.zeitwert.fm.test.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeTestType(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	TYPE_A("type_a", "Type A"),
	TYPE_B("type_b", "Type B"),
	TYPE_C("type_c", "Type C"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTestType>(CodeTestType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		fun getTestType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
