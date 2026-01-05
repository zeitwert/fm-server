package dddrive.domain.oe.persist

import dddrive.ddd.core.model.AggregatePersistenceProvider
import dddrive.domain.oe.model.ObjTenant
import io.crnk.core.queryspec.QuerySpec
import java.util.*

interface ObjTenantPersistenceProvider : AggregatePersistenceProvider<ObjTenant> {

	fun initKernelTenant(
		tenantId: Any,
		userId: Any,
	)

	fun getByKey(key: String): Optional<Any>

	fun find(query: QuerySpec?): List<Any>

}
