package io.zeitwert.fm.oe.model

import io.dddrive.core.obj.model.ObjRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

interface ObjUserFMRepository : ObjRepository<ObjUserFM> {

	val passwordEncoder: PasswordEncoder

	val documentRepository: ObjDocumentRepository

	fun getByEmail(email: String): Optional<ObjUserFM>

	fun isAppAdmin(user: ObjUserFM): Boolean

	fun isAdmin(user: ObjUserFM): Boolean

}
