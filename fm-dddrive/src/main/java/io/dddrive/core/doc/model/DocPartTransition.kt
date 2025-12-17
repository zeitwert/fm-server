package io.dddrive.core.doc.model

import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.oe.model.ObjUser
import java.time.OffsetDateTime

interface DocPartTransition : DocPart<Doc> {

	val seqNr: Int

	val user: ObjUser

	val timestamp: OffsetDateTime

	val oldCaseStage: CodeCaseStage

	val newCaseStage: CodeCaseStage

	fun init(
		userId: Any,
		timestamp: OffsetDateTime,
		oldCaseStage: CodeCaseStage?,
		caseStage: CodeCaseStage,
	)

}
