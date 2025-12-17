package io.dddrive.domain.oe.model

import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.oe.model.ObjTenant
import java.util.*

interface ObjTenantRepository : ObjRepository<ObjTenant> {

	fun getByKey(key: String): Optional<ObjTenant>

	companion object {

		const val KERNEL_TENANT_KEY: String = "k"
	}

}
