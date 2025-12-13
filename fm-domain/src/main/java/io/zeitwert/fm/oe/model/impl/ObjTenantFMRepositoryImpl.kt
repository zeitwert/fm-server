package io.zeitwert.fm.oe.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.oe.model.ObjTenant
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.base.ObjTenantFMBase
import io.zeitwert.fm.oe.persist.jooq.ObjTenantFMPersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objTenantRepository")
class ObjTenantFMRepositoryImpl(
    @Lazy private val userRepository: ObjUserFMRepository,
    @Lazy private val documentRepository: ObjDocumentRepository
) : FMObjRepositoryBase<ObjTenantFM>(
    ObjTenantFMRepository::class.java,
    ObjTenant::class.java,
    ObjTenantFMBase::class.java,
    AGGREGATE_TYPE_ID
), ObjTenantFMRepository {

    private lateinit var persistenceProvider: ObjTenantFMPersistenceProvider

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjTenantFMPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjTenantFM> = persistenceProvider

    override fun getUserRepository(): ObjUserFMRepository = userRepository

    override fun getDocumentRepository(): ObjDocumentRepository = documentRepository

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_tenant"
    }
}
