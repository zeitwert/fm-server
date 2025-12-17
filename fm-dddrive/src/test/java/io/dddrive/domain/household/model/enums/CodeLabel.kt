package io.dddrive.domain.household.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeLabel(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	A("a", "Label A"),
	B("b", "Label B"),
	C("c", "Label C"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeLabel>(CodeLabel::class.java) {
		init {
			CodeLabel.entries.forEach { addItem(it) }
		}
	}
}
