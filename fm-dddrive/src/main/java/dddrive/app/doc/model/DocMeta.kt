package dddrive.app.doc.model

import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.ddd.core.model.AggregateMeta
import java.time.OffsetDateTime

interface DocMeta : dddrive.ddd.core.model.AggregateMeta {

	val docTypeId: String

	val caseDef: CodeCaseDef?

	val caseStages: List<CodeCaseStage>

	val caseStage: CodeCaseStage?

	fun setCaseStage(
		caseStage: CodeCaseStage,
		userId: Any,
		timestamp: OffsetDateTime,
	)

	val isInWork: Boolean

	// availableActions: string[];

	val transitionList: List<DocPartTransition>

}
