package io.zeitwert.fm.oe.model

import dddrive.app.obj.model.ObjRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import java.util.*

interface ObjTenantRepository : ObjRepository<ObjTenant> {

	val userRepository: ObjUserRepository

	val documentRepository: ObjDocumentRepository

	fun getByKey(key: String): Optional<ObjTenant>

	companion object {

		const val KERNEL_TENANT_ID: Int = 1
	}

}
