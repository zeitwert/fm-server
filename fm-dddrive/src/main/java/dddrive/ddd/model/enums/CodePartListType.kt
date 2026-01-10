package dddrive.ddd.model.enums

import dddrive.ddd.model.Enumerated

class CodePartListType(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	override val enumeration: CodePartListTypeEnum
		get() = CodePartListTypeEnum.instance

}
