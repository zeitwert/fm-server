package io.zeitwert.fm.oe.model

import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.FMObjRepository
import java.util.*

interface ObjTenantRepository : FMObjRepository<ObjTenant> {

	val userRepository: ObjUserRepository

	val documentRepository: ObjDocumentRepository

	fun getByKey(key: String): Optional<ObjTenant>

	companion object {

		const val KERNEL_TENANT_ID: Int = 1
	}

}
