package io.zeitwert.fm.oe.model

import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.FMObjRepository
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

interface ObjUserRepository : FMObjRepository<ObjUser> {

	val passwordEncoder: PasswordEncoder

	val documentRepository: ObjDocumentRepository

	fun getByEmail(email: String): Optional<ObjUser>

	fun isAppAdmin(user: ObjUser): Boolean

	fun isAdmin(user: ObjUser): Boolean

}
