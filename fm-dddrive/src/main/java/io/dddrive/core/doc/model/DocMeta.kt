package io.dddrive.core.doc.model

import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseStage
import java.time.OffsetDateTime

interface DocMeta : AggregateMeta {

	val docTypeId: String

	val caseDef: CodeCaseDef?

	val caseStages: List<CodeCaseStage>

	fun setCaseDef(caseDef: CodeCaseDef)

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
