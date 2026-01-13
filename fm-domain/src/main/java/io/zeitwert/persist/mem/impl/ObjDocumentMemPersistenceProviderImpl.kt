package io.zeitwert.persist.mem.impl

import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.persist.ObjDocumentPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjDocument.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objDocumentPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjDocumentMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjDocument>(ObjDocument::class.java),
	ObjDocumentPersistenceProvider {

	// Storage for document content: documentId -> (versionNr -> content data)
	private val contentStorage: MutableMap<Int, MutableMap<Int, ContentData>> = mutableMapOf()

	private data class ContentData(
		val contentType: CodeContentType,
		val content: ByteArray,
	)

	override fun getContentType(document: ObjDocument): CodeContentType? {
		val docId = document.id as? Int ?: return null
		val versions = contentStorage[docId] ?: return null
		val maxVersion = versions.keys.maxOrNull() ?: return null
		return versions[maxVersion]?.contentType
	}

	override fun getContent(document: ObjDocument): ByteArray? {
		val docId = document.id as? Int ?: return null
		val versions = contentStorage[docId] ?: return null
		val maxVersion = versions.keys.maxOrNull() ?: return null
		return versions[maxVersion]?.content
	}

	override fun storeContent(
		document: ObjDocument,
		contentType: CodeContentType?,
		content: ByteArray?,
	) {
		if (contentType == null || content == null) return
		val docId = document.id as? Int ?: return

		val versions = contentStorage.getOrPut(docId) { mutableMapOf() }
		val nextVersion = (versions.keys.maxOrNull() ?: 0) + 1
		versions[nextVersion] = ContentData(contentType, content)
	}
}
