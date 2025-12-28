package dddrive.domain.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.ddd.model.impl.SessionContextImpl
import dddrive.domain.oe.model.ObjTenant
import dddrive.domain.oe.model.ObjTenantRepository
import dddrive.domain.oe.persist.ObjTenantPersistenceProvider
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.util.*

@Component("objTenantRepository")
@DependsOn("objTenantPersistenceProvider")
class ObjTenantRepositoryImpl :
	ObjRepositoryBase<ObjTenant>(
		ObjTenant::class.java,
		AGGREGATE_TYPE,
	),
	ObjTenantRepository {

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjTenant::class.java) as ObjTenantPersistenceProvider

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

	override fun createAggregate(isNew: Boolean): ObjTenant = ObjTenantImpl(this, isNew)

	override fun getByKey(key: String): Optional<ObjTenant> = this.persistenceProvider.getByKey(key).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objTenant"
	}

}
