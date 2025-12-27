package dddrive.app.doc.model

import dddrive.app.doc.model.enums.CodeCaseDef
import dddrive.app.doc.model.enums.CodeCaseStage
import dddrive.app.validation.model.AggregatePartValidation
import java.time.OffsetDateTime

interface DocMeta : dddrive.ddd.core.model.AggregateMeta {

	val docTypeId: String

	// val createdByUser: ObjUser
	val createdByUserId: Any
	val createdAt: OffsetDateTime

	// val modifiedByUser: ObjUser?
	val modifiedByUserId: Any?
	val modifiedAt: OffsetDateTime?

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

	val validationList: List<AggregatePartValidation>

}
