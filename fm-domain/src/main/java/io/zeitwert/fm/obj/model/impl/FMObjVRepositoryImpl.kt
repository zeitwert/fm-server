package io.zeitwert.fm.obj.model.impl

import dddrive.app.obj.model.Obj
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.obj.model.FMObjVRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.obj.persist.FMObjVSqlPersistenceProviderImpl
import org.springframework.stereotype.Component

@Component("objRepository")
class FMObjVRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<Obj>(
		Obj::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMObjVRepository {

	override val persistenceProvider get() = super.persistenceProvider as FMObjVSqlPersistenceProviderImpl

	override fun isObj(id: Any): Boolean = persistenceProvider.isObj(id)

	override fun createAggregate(isNew: Boolean) = ObjVImpl(this, isNew)

	override fun create(): Obj = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(aggregate: Obj) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj"
	}

}
