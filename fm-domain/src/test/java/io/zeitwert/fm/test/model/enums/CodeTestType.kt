package io.zeitwert.fm.test.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeTestType(
	override val defaultName: String,
) : EnumeratedEnum {

	TYPE_A("Type A"),
	TYPE_B("Type B"),
	TYPE_C("Type C"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeTestType>(CodeTestType::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		fun getTestType(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
