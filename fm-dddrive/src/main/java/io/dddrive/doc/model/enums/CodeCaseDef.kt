package io.dddrive.doc.model.enums

import io.dddrive.ddd.model.enums.CodeAggregateType
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum.Companion.getAggregateType
import io.dddrive.enums.model.Enumerated

class CodeCaseDef(
	override val id: String,
	private val name: String,
	private val docTypeId: String,
) : Enumerated {

	private val _caseStages: MutableList<CodeCaseStage> = mutableListOf()
	val docType: CodeAggregateType get() = getAggregateType(docTypeId)

	override val enumeration: CodeCaseDefEnum
		get() = CodeCaseDefEnum.instance

	override fun getName(): String = name

	fun addCaseStage(stage: CodeCaseStage) = _caseStages.add(stage)

	fun getCaseStages(): List<CodeCaseStage> = _caseStages.toList()

}
