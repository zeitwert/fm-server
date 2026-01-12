package dddrive.domain.oe.persist.impl

import dddrive.db.MemoryDb
import dddrive.domain.obj.persist.base.MemObjPersistenceProviderBase
import dddrive.domain.oe.model.ObjUser
import dddrive.domain.oe.model.ObjUserRepository
import dddrive.domain.oe.persist.ObjUserPersistenceProvider
import dddrive.query.query
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

/**
 * Map-based persistence provider for ObjUser.
 *
 * Active when persistence.type=map
 */
@Component("objUserPersistenceProvider")
class MemObjUserPersistenceProviderImpl :
	MemObjPersistenceProviderBase<ObjUser>(ObjUser::class.java),
	ObjUserPersistenceProvider {

	override fun initKernelUser(
		tenantId: Any,
		userId: Any,
	) {
		val kernelUserMap = mapOf(
			"id" to userId,
			"tenantId" to tenantId,
			"version" to 0,
			"maxPartId" to 0,
			"ownerId" to userId,
			"createdAt" to OffsetDateTime.now(),
			"createdByUserId" to userId,
			"modifiedAt" to OffsetDateTime.now(),
			"modifiedByUserId" to userId,
			"email" to ObjUserRepository.KERNEL_USER_EMAIL,
			"caption" to "Kernel User",
			"name" to "Kernel User",
		)
		this.store(kernelUserMap)
	}

	override fun getByEmail(email: String): Optional<Any> {
		val userMap =
			MemoryDb
				.find(intfClass, query { filter { "email" eq email } })
				.firstOrNull()
		return Optional.ofNullable(userMap?.get("id"))
	}

}


