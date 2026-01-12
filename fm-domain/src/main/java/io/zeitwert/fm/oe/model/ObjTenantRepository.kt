package io.zeitwert.fm.oe.model

import dddrive.app.obj.model.ObjRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import java.util.*

interface ObjTenantRepository : ObjRepository<ObjTenant> {

	val userRepository: ObjUserRepository

	val documentRepository: ObjDocumentRepository

	fun getByKey(key: String): Optional<ObjTenant>

	companion object {

		/** Key used to identify the kernel tenant. */
		const val KERNEL_TENANT_KEY: String = "kernel"
	}

}
