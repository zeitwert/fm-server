package dddrive.domain.household.model.enums

import dddrive.ddd.enums.model.EnumeratedEnum
import dddrive.ddd.enums.model.base.EnumerationBase

enum class CodeSalutation(
	override val defaultName: String,
) : EnumeratedEnum {

	MR("Herr"),
	MRS("Frau"),
	;

	override val enumeration get() = Enumeration

	companion object Enumeration : EnumerationBase<CodeSalutation>(CodeSalutation::class.java) {
		init {
			CodeSalutation.entries.forEach { addItem(it) }
		}
	}
}
