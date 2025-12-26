package io.dddrive.doc.model

import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.doc.model.enums.CodeCaseDef
import io.dddrive.doc.model.enums.CodeCaseStage
import java.time.OffsetDateTime

interface DocMeta : AggregateMeta {

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
