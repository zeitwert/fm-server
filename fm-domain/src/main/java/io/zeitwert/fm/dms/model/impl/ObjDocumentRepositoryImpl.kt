package io.zeitwert.fm.dms.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.obj.model.ObjRepository
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.base.ObjDocumentBase
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.persist.jooq.ObjDocumentPersistenceProvider
import io.zeitwert.fm.obj.model.base.FMObjCoreRepositoryBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objDocumentRepository")
class ObjDocumentRepositoryImpl : FMObjCoreRepositoryBase<ObjDocument>(
    ObjDocumentRepository::class.java,
    ObjDocument::class.java,
    ObjDocumentBase::class.java,
    AGGREGATE_TYPE_ID
), ObjDocumentRepository {

    private lateinit var persistenceProvider: ObjDocumentPersistenceProvider
    private lateinit var _accountRepository: ObjAccountRepository

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjDocumentPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    @Autowired
    @Lazy
    fun setAccountRepository(accountRepository: ObjAccountRepository) {
        this._accountRepository = accountRepository
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjDocument> = persistenceProvider

    fun getAccountRepository(): ObjAccountRepository = _accountRepository

    override fun getContent(document: ObjDocument): ByteArray? {
        return persistenceProvider.getContent(document)
    }

    override fun getContentType(document: ObjDocument): CodeContentType? {
        return persistenceProvider.getContentType(document)
    }

    override fun storeContent(document: ObjDocument, contentType: CodeContentType?, content: ByteArray?) {
        persistenceProvider.storeContent(document, contentType, content)
        this.store(document)
    }

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_document"
    }
}

