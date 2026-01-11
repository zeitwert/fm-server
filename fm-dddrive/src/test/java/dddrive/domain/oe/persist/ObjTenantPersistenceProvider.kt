package dddrive.domain.oe.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import dddrive.domain.oe.model.ObjTenant
import java.util.*

interface ObjTenantPersistenceProvider : AggregatePersistenceProvider<ObjTenant> {

	fun initKernelTenant(
		tenantId: Any,
		userId: Any,
	)

	fun getByKey(key: String): Optional<Any>

	fun find(query: QuerySpec?): List<Any>

}
