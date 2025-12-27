package dddrive.ddd.core.model.enums

import dddrive.ddd.enums.model.Enumerated

class CodeAggregateType(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	override val enumeration: CodeAggregateTypeEnum
		get() = CodeAggregateTypeEnum.instance

}
