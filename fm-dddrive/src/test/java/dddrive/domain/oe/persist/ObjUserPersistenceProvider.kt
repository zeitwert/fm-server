package dddrive.domain.oe.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.ddd.query.QuerySpec
import dddrive.domain.oe.model.ObjUser
import java.util.*

interface ObjUserPersistenceProvider : AggregatePersistenceProvider<ObjUser> {

	fun initKernelUser(
		tenantId: Any,
		userId: Any,
	)

	fun getByEmail(email: String): Optional<Any>

	fun find(query: QuerySpec?): List<Any>

}
