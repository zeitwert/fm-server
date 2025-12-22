package io.dddrive.validation.model.enums

import io.dddrive.enums.model.base.EnumerationBase

class CodeValidationLevelEnum : EnumerationBase<CodeValidationLevel>(CodeValidationLevel::class.java) {

	init {
		instance = this
		this.addItem(CodeValidationLevel("info", "Info"))
		this.addItem(CodeValidationLevel("warning", "Warning"))
		this.addItem(CodeValidationLevel("error", "Error"))
	}

	override fun assignItems() {
		INFO = getValidationLevel("info")
		WARNING = getValidationLevel("warning")
		ERROR = getValidationLevel("error")
	}

	companion object {

		lateinit var INFO: CodeValidationLevel

		lateinit var WARNING: CodeValidationLevel

		lateinit var ERROR: CodeValidationLevel

		lateinit var instance: CodeValidationLevelEnum

		@JvmStatic
		fun getValidationLevel(validationLevelId: String): CodeValidationLevel = instance.getItem(validationLevelId)

	}

}
