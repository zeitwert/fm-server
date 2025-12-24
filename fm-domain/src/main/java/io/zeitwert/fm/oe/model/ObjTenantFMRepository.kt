package io.zeitwert.fm.oe.model

import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.FMObjRepository

interface ObjTenantFMRepository : FMObjRepository<ObjTenantFM> {

	val userRepository: ObjUserFMRepository

	val documentRepository: ObjDocumentRepository

	companion object {

		const val KERNEL_TENANT_ID: Int = 1
	}

}
