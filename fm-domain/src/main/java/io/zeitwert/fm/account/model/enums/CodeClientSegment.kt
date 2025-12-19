package io.zeitwert.fm.account.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeClientSegment(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	COMMUNITY("community", "Gemeinde"),
	FAMILY("family", "Family Office"),
	;

	override fun getName() = itemName

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeClientSegment>(CodeClientSegment::class.java) {
		init {
			entries.forEach { addItem(it) }
		}

		fun getClientSegment(itemId: String): CodeClientSegment? = getItem(itemId)
	}
}
