package io.zeitwert.fm.dms.model

import io.dddrive.core.obj.model.Obj
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.task.model.ItemWithTasks
import java.time.OffsetDateTime

interface ObjDocument :
	Obj,
	ItemWithAccount,
	ItemWithNotes,
	ItemWithTasks {

	var name: String?

	var contentKind: CodeContentKind?

	var documentKind: CodeDocumentKind?

	var documentCategory: CodeDocumentCategory?

	var templateDocumentId: Int?

	val templateDocument: ObjDocument?

	val contentType: CodeContentType?

	val content: ByteArray?

	fun storeContent(
		contentType: CodeContentType,
		content: ByteArray,
		userId: Any,
		timestamp: OffsetDateTime,
	)

}
