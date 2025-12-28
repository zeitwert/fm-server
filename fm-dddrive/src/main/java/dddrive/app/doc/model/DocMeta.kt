package dddrive.app.doc.model

import dddrive.app.ddd.model.AggregateMeta
import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseStage
import java.time.OffsetDateTime

interface DocMeta : AggregateMeta {

	val docTypeId: String

	val caseDef: CodeCaseDef?
	val caseStages: List<CodeCaseStage>
	val caseStage: CodeCaseStage?
	val isInWork: Boolean
	// availableActions: string[];

	fun setCaseStage(
		caseStage: CodeCaseStage,
		userId: Any,
		timestamp: OffsetDateTime,
	)

	val transitionList: List<DocPartTransition>

}
