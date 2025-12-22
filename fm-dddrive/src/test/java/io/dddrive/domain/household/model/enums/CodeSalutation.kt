package io.dddrive.domain.household.model.enums

import io.dddrive.enums.model.Enumerated
import io.dddrive.enums.model.base.EnumerationBase

enum class CodeSalutation(
	override val id: String,
	private val itemName: String,
) : Enumerated {

	MR("mr", "Herr"),
	MRS("mrs", "Frau"),
	;

	override val enumeration get() = Enumeration

	override fun getName() = itemName

	companion object Enumeration : EnumerationBase<CodeSalutation>(CodeSalutation::class.java) {
		init {
			CodeSalutation.entries.forEach { addItem(it) }
		}
	}
}
