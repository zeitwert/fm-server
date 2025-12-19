package io.zeitwert.fm.oe.model.impl

import io.dddrive.core.oe.model.ObjTenant
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.base.ObjTenantFMBase
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objTenantRepository")
class ObjTenantFMRepositoryImpl(
	@param:Lazy override val userRepository: ObjUserFMRepository,
	@param:Lazy override val documentRepository: ObjDocumentRepository,
) : FMObjRepositoryBase<ObjTenantFM>(
	ObjTenantFMRepository::class.java,
	ObjTenant::class.java,
	ObjTenantFMBase::class.java,
	AGGREGATE_TYPE_ID,
),
	ObjTenantFMRepository {

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_tenant"
	}

}
