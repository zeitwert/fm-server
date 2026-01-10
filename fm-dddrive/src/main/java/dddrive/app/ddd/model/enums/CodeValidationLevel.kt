package dddrive.app.ddd.model.enums

import dddrive.ddd.model.Enumerated

class CodeValidationLevel(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	override val enumeration: CodeValidationLevelEnum
		get() = CodeValidationLevelEnum.instance

}
