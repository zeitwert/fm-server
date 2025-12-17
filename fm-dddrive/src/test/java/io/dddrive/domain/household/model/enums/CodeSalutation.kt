package io.dddrive.domain.household.model.enums

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.base.EnumerationBase

enum class CodeSalutation(
	private val id: String,
	private val itemName: String,
) : Enumerated {

	MR("mr", "Herr"),
	MRS("mrs", "Frau"),
	;

	override fun getId() = id

	override fun getName() = itemName

	override fun getEnumeration() = Enumeration

	companion object Enumeration : EnumerationBase<CodeSalutation>(CodeSalutation::class.java) {
		init {
			CodeSalutation.entries.forEach { addItem(it) }
		}
	}
}
