package io.zeitwert.fm.collaboration.model.impl

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase
import io.zeitwert.fm.collaboration.persist.jooq.ObjNotePersistenceProvider
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjNote using the NEW dddrive framework.
 */
@Component("objNoteRepository")
class ObjNoteRepositoryImpl : FMObjRepositoryBase<ObjNote>(
    ObjNoteRepository::class.java,
    ObjNote::class.java,
    ObjNoteBase::class.java,
    AGGREGATE_TYPE_ID
), ObjNoteRepository {

    private lateinit var persistenceProvider: ObjNotePersistenceProvider

    @Autowired
    @Lazy
    fun setPersistenceProvider(persistenceProvider: ObjNotePersistenceProvider) {
        this.persistenceProvider = persistenceProvider
    }

    override fun getPersistenceProvider(): AggregatePersistenceProvider<ObjNote> = persistenceProvider

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_note"
    }
}

