package io.zeitwert.fm.doc.model.base

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.DocRepository
import io.dddrive.core.doc.model.base.DocRepositoryBase
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired

/**
 * Base repository class for FM Doc entities using the NEW dddrive framework.
 *
 * This class extends the new dddrive DocRepositoryBase and adds:
 * - DSLContext injection for jOOQ operations
 * - Persistence provider wiring
 *
 * Subclasses must implement getPersistenceProvider() to return their specific provider.
 *
 * @param D The Doc entity type
 */
abstract class FMDocCoreRepositoryBase<D : Doc>(
    repoIntfClass: Class<out DocRepository<D>>,
    intfClass: Class<out Doc>,
    baseClass: Class<out Doc>,
    aggregateTypeId: String
) : DocRepositoryBase<D>(repoIntfClass, intfClass, baseClass, aggregateTypeId) {

    private lateinit var _dslContext: DSLContext

    @Autowired
    fun setDslContext(dslContext: DSLContext) {
        this._dslContext = dslContext
    }

    fun dslContext(): DSLContext = _dslContext

    /**
     * Subclasses must implement this to return their persistence provider.
     */
    abstract override fun getPersistenceProvider(): AggregatePersistenceProvider<D>
}

