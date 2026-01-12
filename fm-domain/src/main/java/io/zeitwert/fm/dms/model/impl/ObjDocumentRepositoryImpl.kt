package io.zeitwert.fm.dms.model.impl

import io.zeitwert.app.obj.model.base.FMObjRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.persist.ObjDocumentPersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("objDocumentRepository")
class ObjDocumentRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjDocument>(
		ObjDocument::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjDocumentRepository {

	private lateinit var accountRepository: ObjAccountRepository

	override fun createAggregate(isNew: Boolean): ObjDocument = ObjDocumentImpl(this, isNew)

	private val documentPersistenceProvider get() = persistenceProvider as ObjDocumentPersistenceProvider

	@Autowired
	@Lazy
	fun setAccountRepository(accountRepository: ObjAccountRepository) {
		this.accountRepository = accountRepository
	}

	override fun getContent(document: ObjDocument): ByteArray? = documentPersistenceProvider.getContent(document)

	override fun getContentType(document: ObjDocument): CodeContentType? = documentPersistenceProvider.getContentType(document)

	override fun storeContent(
		document: ObjDocument,
		contentType: CodeContentType,
		content: ByteArray,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		documentPersistenceProvider.storeContent(document, contentType, content)
// 		this.store(document, directory.getRepository(ObjUser::class.java).get(userId), timestamp)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_document"
	}

}
