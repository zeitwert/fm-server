package io.dddrive.domain.oe.model

import io.dddrive.core.obj.model.ObjRepository
import io.dddrive.core.oe.model.ObjUser
import java.util.*

interface ObjUserRepository : ObjRepository<ObjUser> {

	fun getByEmail(email: String): Optional<ObjUser>

	companion object {

		const val KERNEL_USER_EMAIL: String = "k@dddrive.io"
	}
}
