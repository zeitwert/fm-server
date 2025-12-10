package io.zeitwert.fm.account.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.base.ObjAccountBase
import io.zeitwert.fm.account.persist.jooq.ObjAccountPersistenceProvider
import io.zeitwert.fm.contact.model.ObjContactRepository
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjCoreRepositoryBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjAccount using the NEW dddrive framework.
 */
@Component("objAccountRepository")
class ObjAccountRepositoryImpl : FMObjCoreRepositoryBase<ObjAccount>(
    ObjAccountRepository::class.java,
    ObjAccount::class.java,
    ObjAccountBase::class.java,
    AGGREGATE_TYPE_ID
), ObjAccountRepository {

    private lateinit var persistenceProvider: ObjAccountPersistenceProvider
    private lateinit var _contactRepository: ObjContactRepository

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private lateinit var _documentRepository: ObjDocumentRepository

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjAccountPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    @Autowired
    @Lazy
    fun setContactRepository(contactRepository: ObjContactRepository) {
        this._contactRepository = contactRepository
    }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // @Autowired
    // @Lazy
    // fun setDocumentRepository(documentRepository: ObjDocumentRepository) {
    //     this._documentRepository = documentRepository
    // }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjAccount> = persistenceProvider

    override fun getContactRepository(): ObjContactRepository = _contactRepository

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getDocumentRepository(): ObjDocumentRepository = _documentRepository

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_account"
    }
}

