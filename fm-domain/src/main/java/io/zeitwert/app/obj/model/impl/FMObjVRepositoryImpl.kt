package io.zeitwert.app.obj.model.impl

import dddrive.app.obj.model.Obj
import io.zeitwert.app.obj.model.FMObjVRepository
import io.zeitwert.app.obj.model.base.FMObjRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.sql.ObjSqlPersistenceProviderImpl
import org.springframework.stereotype.Component

@Component("objRepository")
class FMObjVRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<Obj>(
		Obj::class.java,
		AGGREGATE_TYPE_ID,
	),
	FMObjVRepository {

	override val persistenceProvider get() = super.persistenceProvider as ObjSqlPersistenceProviderImpl

	override fun isObj(id: Any): Boolean = persistenceProvider.isObj(id)

	override fun createAggregate(isNew: Boolean) = ObjVImpl(this, isNew)

	override fun create(): Obj = throw UnsupportedOperationException("this is a readonly repository")

	override fun store(aggregate: Obj) = throw UnsupportedOperationException("this is a readonly repository")

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj"
	}

}
