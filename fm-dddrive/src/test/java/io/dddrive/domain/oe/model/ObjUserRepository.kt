package io.dddrive.domain.oe.model

import io.dddrive.obj.model.ObjRepository
import io.dddrive.oe.model.ObjUser
import java.util.*

interface ObjUserRepository : ObjRepository<ObjUser> {

	fun getByEmail(email: String): Optional<ObjUser>

	companion object {

		const val KERNEL_USER_EMAIL: String = "k@dddrive.io"
	}
}
