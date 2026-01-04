package dddrive.app.doc.model.enums

import dddrive.ddd.core.model.enums.CodeAggregateType
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum.Companion.getAggregateType
import dddrive.ddd.enums.model.Enumerated

class CodeCaseDef(
	override val id: String,
	override val defaultName: String,
	private val docTypeId: String,
) : Enumerated {

	val docType: CodeAggregateType get() = getAggregateType(docTypeId)

	private val _caseStages: MutableList<CodeCaseStage> = mutableListOf()

	override val enumeration: CodeCaseDefEnum
		get() = CodeCaseDefEnum.instance

	fun addCaseStage(stage: CodeCaseStage) = _caseStages.add(stage)

	val caseStages: List<CodeCaseStage> get() = _caseStages.toList()

}
