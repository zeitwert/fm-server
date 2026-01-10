package dddrive.app.doc.model.enums

import dddrive.ddd.model.Enumerated

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

	init {
		CodeCaseDefEnum.getCaseDef(caseDefId).addCaseStage(this)
	}

	override val enumeration: CodeCaseStageEnum
		get() = CodeCaseStageEnum.instance

	val caseDef: CodeCaseDef
		get() = CodeCaseDefEnum.getCaseDef(this.caseDefId)

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
