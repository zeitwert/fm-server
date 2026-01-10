package dddrive.ddd.model.enums

import dddrive.ddd.model.Enumerated

class CodeAggregateType(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	override val enumeration: CodeAggregateTypeEnum
		get() = CodeAggregateTypeEnum.instance

}
