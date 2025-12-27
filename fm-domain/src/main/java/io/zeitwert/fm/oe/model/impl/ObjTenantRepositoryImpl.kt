package io.zeitwert.fm.oe.model.impl

import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUserRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objTenantRepository")
class ObjTenantRepositoryImpl(
	@param:Lazy override val userRepository: ObjUserRepository,
	@param:Lazy override val documentRepository: ObjDocumentRepository,
) : FMObjRepositoryBase<ObjTenant>(
		ObjTenant::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjTenantRepository {

	override fun createAggregate(isNew: Boolean): ObjTenant = ObjTenantImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_tenant"
	}

}
