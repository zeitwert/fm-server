package io.zeitwert.fm.test.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository
import io.zeitwert.fm.test.model.ObjTestRepository
import io.zeitwert.fm.test.model.base.ObjTestBase
import io.zeitwert.fm.test.persist.jooq.ObjTestPersistenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjTest using the NEW dddrive framework.
 */
@Component("objTestRepository")
class ObjTestRepositoryImpl : FMObjRepositoryBase<ObjTest>(
    ObjTestRepository::class.java,
    ObjTest::class.java,
    ObjTestBase::class.java,
    AGGREGATE_TYPE_ID
), ObjTestRepository {

    private lateinit var persistenceProvider: ObjTestPersistenceProvider
    private lateinit var nodeRepository: ObjTestPartNodeRepository
    private lateinit var noteRepository: ObjNoteRepository

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjTestPersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    @Autowired
    @Lazy
    fun setNodeRepository(nodeRepository: ObjTestPartNodeRepository) {
        this.nodeRepository = nodeRepository
    }

    @Autowired
    @Lazy
    fun setNoteRepository(noteRepository: ObjNoteRepository) {
        this.noteRepository = noteRepository
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjTest> = persistenceProvider

    override fun getNodeRepository(): ObjTestPartNodeRepository = nodeRepository

    fun getNoteRepository(): ObjNoteRepository = noteRepository

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_test"
    }
}

