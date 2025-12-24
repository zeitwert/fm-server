package io.zeitwert.fm.dms.model

import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.obj.model.FMObjRepository
import java.time.OffsetDateTime

interface ObjDocumentRepository : FMObjRepository<ObjDocument> {

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
