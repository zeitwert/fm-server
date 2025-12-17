package io.dddrive.domain.oe.persist

import io.dddrive.core.ddd.model.AggregatePersistenceProvider
import io.dddrive.core.oe.model.ObjTenant
import java.util.*

interface ObjTenantPersistenceProvider : AggregatePersistenceProvider<ObjTenant> {

	fun initKernelTenant(
		tenantId: Any,
		userId: Any,
	)

	fun getByKey(key: String): Optional<Any>

}
