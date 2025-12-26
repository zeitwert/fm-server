package io.zeitwert.fm.account.model.enums

import io.dddrive.enums.model.EnumeratedEnum
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeClientSegment(
	override val defaultName: String,
) : EnumeratedEnum {

	COMMUNITY("Gemeinde"),
	FAMILY("Family Office"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeClientSegment>(CodeClientSegment::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		fun getClientSegment(itemId: String?) = if (itemId != null) getItem(itemId) else null
	}
}
