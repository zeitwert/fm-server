package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentType

interface ObjDocumentPersistenceProvider : AggregatePersistenceProvider<ObjDocument> {

	fun getContentType(document: ObjDocument): CodeContentType?

	fun getContent(document: ObjDocument): ByteArray?

	fun storeContent(
		document: ObjDocument,
		contentType: CodeContentType?,
		content: ByteArray?,
	)

}
