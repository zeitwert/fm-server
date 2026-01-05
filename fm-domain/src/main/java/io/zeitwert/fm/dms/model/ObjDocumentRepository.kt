package io.zeitwert.fm.dms.model

import dddrive.app.obj.model.ObjRepository
import io.zeitwert.fm.dms.model.enums.CodeContentType
import java.time.OffsetDateTime

interface ObjDocumentRepository : ObjRepository<ObjDocument> {

	fun getContent(document: ObjDocument): ByteArray?

	fun getContentType(document: ObjDocument): CodeContentType?

	fun storeContent(
		document: ObjDocument,
		contentType: CodeContentType,
		content: ByteArray,
		userId: Any,
		timestamp: OffsetDateTime,
	)

}
