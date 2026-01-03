package dddrive.domain.obj.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.obj.persist.base.MapObjPersistenceProviderBase
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component("objRepository")
class ObjRepositoryImpl(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : ObjRepositoryBase<Obj>(
		Obj::class.java,
		AGGREGATE_TYPE,
	),
	ObjRepository<Obj> {

	override val sessionContext: SessionContext get() = sessionContextProvider.getObject()

	override val persistenceProvider = object : MapObjPersistenceProviderBase<Obj>(Obj::class.java) {}

	override fun createAggregate(isNew: Boolean): Obj = TODO()

	companion object {

		private const val AGGREGATE_TYPE = "obj"
	}

}
