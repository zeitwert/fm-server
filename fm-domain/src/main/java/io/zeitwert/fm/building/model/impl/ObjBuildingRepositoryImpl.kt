package io.zeitwert.fm.building.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.base.ObjBuildingBase
import io.zeitwert.fm.building.persist.jooq.ObjBuildingPersistenceProvider
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objBuildingRepository")
class ObjBuildingRepositoryImpl : FMObjRepositoryBase<ObjBuilding>(
    ObjBuildingRepository::class.java,
    ObjBuilding::class.java,
    ObjBuildingBase::class.java,
    AGGREGATE_TYPE_ID
), ObjBuildingRepository {

    private lateinit var persistenceProvider: ObjBuildingPersistenceProvider
    private lateinit var _accountRepository: ObjAccountRepository
    private lateinit var _contactRepository: ObjContactRepository
    private lateinit var _documentRepository: ObjDocumentRepository
    private lateinit var _taskRepository: DocTaskRepository

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjBuildingPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    @Autowired
    @Lazy
    fun setAccountRepository(accountRepository: ObjAccountRepository) {
        this._accountRepository = accountRepository
    }

    @Autowired
    @Lazy
    fun setContactRepository(contactRepository: ObjContactRepository) {
        this._contactRepository = contactRepository
    }

    @Autowired
    @Lazy
    fun setDocumentRepository(documentRepository: ObjDocumentRepository) {
        this._documentRepository = documentRepository
    }

    @Autowired
    @Lazy
    fun setTaskRepository(taskRepository: DocTaskRepository) {
        this._taskRepository = taskRepository
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjBuilding> = persistenceProvider

    override fun getAccountRepository(): ObjAccountRepository = _accountRepository

    override fun getContactRepository(): ObjContactRepository = _contactRepository

    override fun getDocumentRepository(): ObjDocumentRepository = _documentRepository

    override fun getTaskRepository(): DocTaskRepository = _taskRepository

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_building"
    }
}

