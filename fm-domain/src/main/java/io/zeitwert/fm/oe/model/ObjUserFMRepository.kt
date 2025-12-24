package io.zeitwert.fm.oe.model

import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.FMObjRepository
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

interface ObjUserFMRepository : FMObjRepository<ObjUserFM> {

	val passwordEncoder: PasswordEncoder

	val documentRepository: ObjDocumentRepository

	fun getByEmail(email: String): Optional<ObjUserFM>

	fun isAppAdmin(user: ObjUserFM): Boolean

	fun isAdmin(user: ObjUserFM): Boolean

}
