package dddrive.ddd.validation.model.enums

import dddrive.ddd.enums.model.Enumerated

class CodeValidationLevel(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	override val enumeration: CodeValidationLevelEnum
		get() = CodeValidationLevelEnum.instance

}
