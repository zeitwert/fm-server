package io.zeitwert.fm.task.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.account.service.api.ObjAccountCache
import io.zeitwert.fm.doc.model.base.FMDocCoreRepositoryBase
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.base.DocTaskBase
import io.zeitwert.fm.task.persist.jooq.DocTaskPersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("docTaskRepository")
class DocTaskRepositoryImpl(
    private val _accountCache: ObjAccountCache
) : FMDocCoreRepositoryBase<DocTask>(
    DocTaskRepository::class.java,
    DocTask::class.java,
    DocTaskBase::class.java,
    AGGREGATE_TYPE_ID
), DocTaskRepository {

    private lateinit var persistenceProvider: DocTaskPersistenceProvider

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: DocTaskPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<DocTask> = persistenceProvider

    override fun getAccountCache(): ObjAccountCache = _accountCache

    companion object {
        private const val AGGREGATE_TYPE_ID = "doc_task"
    }
}

