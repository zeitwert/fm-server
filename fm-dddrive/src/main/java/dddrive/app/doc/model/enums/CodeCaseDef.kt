package dddrive.app.doc.model.enums

import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum.Companion.getAggregateType
import dddrive.ddd.enums.model.Enumerated

class CodeCaseDef(
	override val id: String,
	override val defaultName: String,
	private val docTypeId: String,
) : Enumerated {

	private val _caseStages: MutableList<CodeCaseStage> = mutableListOf()
	val docType: dddrive.ddd.core.model.enums.CodeAggregateType get() = getAggregateType(docTypeId)

	override val enumeration: CodeCaseDefEnum
		get() = CodeCaseDefEnum.instance

	fun addCaseStage(stage: CodeCaseStage) = _caseStages.add(stage)

	fun getCaseStages(): List<CodeCaseStage> = _caseStages.toList()

}
