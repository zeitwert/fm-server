package dddrive.domain.oe.model

import dddrive.app.obj.model.ObjRepository
import java.util.*

interface ObjUserRepository : ObjRepository<ObjUser> {

	fun getByEmail(email: String): Optional<ObjUser>

	companion object {

		const val KERNEL_USER_EMAIL: String = "k@dddrive.io"
	}
}
