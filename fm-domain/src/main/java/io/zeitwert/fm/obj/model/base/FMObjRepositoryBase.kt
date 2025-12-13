package io.zeitwert.fm.obj.model.base

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.obj.model.base.ObjRepositoryBase
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired

/**
 * Base repository class for FM Obj entities using the NEW dddrive framework.
 *
 * This class extends the new dddrive ObjRepositoryBase and adds:
 * - DSLContext injection for jOOQ operations
 * - Persistence provider wiring
 * - ObjNoteRepository for collaboration notes
 *
 * Subclasses must implement getPersistenceProvider() to return their specific provider.
 *
 * @param O The Obj entity type
 */
abstract class FMObjRepositoryBase<O : Obj>(
	repoIntfClass: Class<out ObjRepository<O>>,
	intfClass: Class<out Obj>,
	baseClass: Class<out Obj>,
	aggregateTypeId: String
) : ObjRepositoryBase<O>(repoIntfClass, intfClass, baseClass, aggregateTypeId) {

	private lateinit var _dslContext: DSLContext

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	fun dslContext(): DSLContext = _dslContext

	/**
	 * Subclasses must implement this to return their persistence provider.
	 */
	abstract override fun getPersistenceProvider(): AggregatePersistenceProvider<O>

}
