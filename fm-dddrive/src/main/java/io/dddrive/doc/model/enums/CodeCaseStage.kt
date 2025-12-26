package io.dddrive.doc.model.enums

import io.dddrive.doc.model.enums.CodeCaseDefEnum.Companion.getCaseDef
import io.dddrive.enums.model.Enumerated

class CodeCaseStage(
	override val id: String,
	override val defaultName: String,
	private val caseDefId: String,
	val caseStageTypeId: String?,
	private val name: String,
	val description: String?,
	val seqNr: Int?,
	private val abstractCaseStageId: String?,
	val action: String?,
	val availableActions: List<String>?,
) : Enumerated {

	override val enumeration: CodeCaseStageEnum
		get() = CodeCaseStageEnum.instance

	val caseDef: CodeCaseDef
		get() = getCaseDef(this.caseDefId)

	val isInWork: Boolean
		get() = "terminal" != this.caseStageTypeId

	val isAbstract: Boolean
		get() = "abstract" == this.caseStageTypeId

	val abstractCaseStage: CodeCaseStage?
		get() =
			if (this.abstractCaseStageId != null) {
				CodeCaseStageEnum.getCaseStage(this.abstractCaseStageId)
			} else {
				null
			}

}
