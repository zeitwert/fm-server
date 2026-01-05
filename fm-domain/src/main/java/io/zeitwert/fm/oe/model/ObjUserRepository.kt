package io.zeitwert.fm.oe.model

import dddrive.app.obj.model.ObjRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

interface ObjUserRepository : ObjRepository<ObjUser> {

	val passwordEncoder: PasswordEncoder

	val documentRepository: ObjDocumentRepository

	fun getByEmail(email: String): Optional<ObjUser>

	fun isAppAdmin(user: ObjUser): Boolean

	fun isAdmin(user: ObjUser): Boolean

}
