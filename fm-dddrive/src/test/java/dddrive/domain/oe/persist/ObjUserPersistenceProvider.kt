package dddrive.domain.oe.persist

import dddrive.ddd.core.model.AggregatePersistenceProvider
import io.dddrive.oe.model.ObjUser
import java.util.*

interface ObjUserPersistenceProvider : AggregatePersistenceProvider<ObjUser> {

	fun initKernelUser(
		tenantId: Any,
		userId: Any,
	)

	fun getByEmail(email: String): Optional<Any>

}
