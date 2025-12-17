package io.dddrive.core.ddd.model.enums

import io.dddrive.core.enums.model.Enumerated

class CodeAggregateType(
	override val id: String,
	private val name: String,
) : Enumerated {

	override val enumeration: CodeAggregateTypeEnum
		get() = CodeAggregateTypeEnum.getInstance()

	override fun getName(): String = name

}
