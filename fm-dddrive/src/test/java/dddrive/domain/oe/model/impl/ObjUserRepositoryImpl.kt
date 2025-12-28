package dddrive.domain.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.ddd.model.impl.SessionContextImpl
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import dddrive.domain.oe.persist.ObjUserPersistenceProvider
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
@DependsOn("objUserPersistenceProvider")
class ObjUserRepositoryImpl :
	ObjRepositoryBase<ObjUser>(
		ObjUser::class.java,
		AGGREGATE_TYPE,
	),
	ObjUserRepository {

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjUser::class.java) as ObjUserPersistenceProvider

	override lateinit var sessionContext: SessionContext

	fun initSessionContext(
		tenantId: Any,
		accountId: Any,
		userId: Any,
	) {
		sessionContext = SessionContextImpl(
			tenantId = tenantId,
			accountId = accountId,
			userId = userId,
		)
	}

	override fun createAggregate(isNew: Boolean): ObjUser = ObjUserImpl(this, isNew)

	override fun getByEmail(email: String): Optional<ObjUser> = this.persistenceProvider.getByEmail(email).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objUser"
	}

}
