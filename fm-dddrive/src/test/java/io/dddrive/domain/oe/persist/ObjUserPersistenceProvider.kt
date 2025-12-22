package io.dddrive.domain.oe.persist

import io.dddrive.ddd.model.AggregatePersistenceProvider
import io.dddrive.oe.model.ObjUser
import java.util.*

interface ObjUserPersistenceProvider : AggregatePersistenceProvider<ObjUser> {

	fun initKernelUser(
		tenantId: Any,
		userId: Any,
	)

	fun getByEmail(email: String): Optional<Any>

}
