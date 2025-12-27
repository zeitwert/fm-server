package dddrive.domain.household.model.enums

import dddrive.ddd.enums.model.EnumeratedEnum
import dddrive.ddd.enums.model.base.EnumerationBase

enum class CodeLabel(
	override val defaultName: String,
) : EnumeratedEnum {

	A("Label A"),
	B("Label B"),
	C("Label C"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeLabel>(CodeLabel::class.java) {
		init {
			CodeLabel.entries.forEach { addItem(it) }
		}
	}
}
