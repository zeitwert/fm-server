package io.zeitwert.fm.dms.model.impl

import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.base.ObjDocumentBase
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.persist.jooq.ObjDocumentPersistenceProvider
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("objDocumentRepository")
class ObjDocumentRepositoryImpl : FMObjRepositoryBase<ObjDocument>(
	ObjDocumentRepository::class.java,
	ObjDocument::class.java,
	ObjDocumentBase::class.java,
	AGGREGATE_TYPE_ID
), ObjDocumentRepository {

	private lateinit var accountRepository: ObjAccountRepository

	override val persistenceProvider get() = super.persistenceProvider as ObjDocumentPersistenceProvider

	@Autowired
	@Lazy
	fun setAccountRepository(accountRepository: ObjAccountRepository) {
		this.accountRepository = accountRepository
	}

	override fun getContent(document: ObjDocument): ByteArray? {
		return persistenceProvider.getContent(document)
	}

	override fun getContentType(document: ObjDocument): CodeContentType? {
		return persistenceProvider.getContentType(document)
	}

	override fun storeContent(
		document: ObjDocument,
		contentType: CodeContentType,
		content: ByteArray,
		userId: Any,
		timestamp: OffsetDateTime
	) {
		persistenceProvider.storeContent(document, contentType, content)
//		this.store(document, directory.getRepository(ObjUser::class.java).get(userId), timestamp)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_document"
	}

}
