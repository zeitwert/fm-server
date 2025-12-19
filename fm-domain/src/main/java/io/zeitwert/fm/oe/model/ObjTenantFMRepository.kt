package io.zeitwert.fm.oe.model

import io.dddrive.core.obj.model.ObjRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository

interface ObjTenantFMRepository : ObjRepository<ObjTenantFM> {

	val userRepository: ObjUserFMRepository

	val documentRepository: ObjDocumentRepository

	companion object {

		const val KERNEL_TENANT_ID: Int = 1
	}

}
