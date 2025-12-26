package io.dddrive.validation.model.enums

import io.dddrive.enums.model.Enumerated

class CodeValidationLevel(
	override val id: String,
	override val defaultName: String,
) : Enumerated {

	override val enumeration: CodeValidationLevelEnum
		get() = CodeValidationLevelEnum.Companion.instance

}
