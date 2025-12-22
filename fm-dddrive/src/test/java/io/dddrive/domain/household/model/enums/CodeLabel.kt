package io.dddrive.domain.household.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeLabel(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	A("a", "Label A"),
	B("b", "Label B"),
	C("c", "Label C"),
	;

	override val enumeration get() = Enumeration

	override fun getName() = itemName

	companion object Enumeration : EnumerationBase<CodeLabel>(CodeLabel::class.java) {
		init {
			CodeLabel.entries.forEach { addItem(it) }
		}
	}
}
