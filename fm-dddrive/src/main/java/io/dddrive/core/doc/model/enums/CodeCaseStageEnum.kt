package io.dddrive.core.doc.model.enums

import io.dddrive.core.enums.model.base.EnumerationBase

class CodeCaseStageEnum : EnumerationBase<CodeCaseStage>(CodeCaseStage::class.java) {

	init {
		instance = this
	}

	companion object {

		lateinit var instance: CodeCaseStageEnum

		@JvmStatic
		fun getCaseStage(caseStageId: String): CodeCaseStage = instance.getItem(caseStageId)

	}

}
