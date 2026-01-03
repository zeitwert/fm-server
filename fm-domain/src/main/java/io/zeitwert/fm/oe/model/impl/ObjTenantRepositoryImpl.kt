package io.zeitwert.fm.oe.model.impl

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.persist.ObjTenantSqlPersistenceProviderImpl
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.util.*

@Component("objTenantRepository")
class ObjTenantRepositoryImpl(
	@param:Lazy override val userRepository: ObjUserRepository,
	@param:Lazy override val documentRepository: ObjDocumentRepository,
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjTenant>(
		ObjTenant::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjTenantRepository {

	override fun createAggregate(isNew: Boolean): ObjTenant = ObjTenantImpl(this, isNew)

	override fun getByKey(key: String): Optional<ObjTenant> {
		val tenantId = (persistenceProvider as ObjTenantSqlPersistenceProviderImpl).getByKey(key)
		return tenantId.map { id -> get(id) }
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_tenant"
	}

}
