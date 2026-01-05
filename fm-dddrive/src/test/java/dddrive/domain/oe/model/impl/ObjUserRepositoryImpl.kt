package dddrive.domain.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import dddrive.ddd.query.QuerySpec
import dddrive.domain.oe.persist.ObjUserPersistenceProvider
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
class ObjUserRepositoryImpl(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : ObjRepositoryBase<ObjUser>(
		ObjUser::class.java,
		AGGREGATE_TYPE,
	),
	ObjUserRepository {

	override val sessionContext: SessionContext get() = sessionContextProvider.getObject()

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjUser::class.java) as ObjUserPersistenceProvider

	override fun createAggregate(isNew: Boolean): ObjUser = ObjUserImpl(this, isNew)

	override fun find(query: QuerySpec?): List<Any> = persistenceProvider.find(query)

	override fun getByEmail(email: String): Optional<ObjUser> = this.persistenceProvider.getByEmail(email).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objUser"
	}

}
