package io.zeitwert.fm.oe.model.impl

import io.dddrive.oe.model.ObjTenant
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objTenantRepository")
class ObjTenantFMRepositoryImpl(
	@param:Lazy override val userRepository: ObjUserFMRepository,
	@param:Lazy override val documentRepository: ObjDocumentRepository,
) : FMObjRepositoryBase<ObjTenantFM>(
		ObjTenant::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjTenantFMRepository {

	override fun createAggregate(isNew: Boolean): ObjTenantFM = ObjTenantFMImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_tenant"
	}

}
