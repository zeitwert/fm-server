package io.dddrive.core.doc.model.enums

import io.dddrive.core.enums.model.base.EnumerationBase

class CodeCaseDefEnum : EnumerationBase<CodeCaseDef>(CodeCaseDef::class.java) {

	init {
		instance = this
	}

	companion object {

		lateinit var instance: CodeCaseDefEnum

		@JvmStatic
		fun getCaseDef(caseDefId: String): CodeCaseDef = instance.getItem(caseDefId)

	}

}
