package io.zeitwert.fm.oe.model.impl

import io.dddrive.core.oe.model.ObjUser
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.base.ObjUserFMBase
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import io.zeitwert.fm.oe.persist.jooq.ObjUserFMPersistenceProviderImpl
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
class ObjUserFMRepositoryImpl(
	@param:Lazy override val passwordEncoder: PasswordEncoder,
	@param:Lazy override val documentRepository: ObjDocumentRepository,
) : FMObjRepositoryBase<ObjUserFM>(
	ObjUserFMRepository::class.java,
	ObjUser::class.java,
	ObjUserFMBase::class.java,
	AGGREGATE_TYPE_ID,
),
	ObjUserFMRepository {

	override fun isAppAdmin(user: ObjUserFM): Boolean = user.hasRole(CodeUserRole.APP_ADMIN)

	override fun isAdmin(user: ObjUserFM): Boolean = user.hasRole(CodeUserRole.ADMIN)

	override fun getByEmail(email: String): Optional<ObjUserFM> {
		val userId = (persistenceProvider as ObjUserFMPersistenceProviderImpl).getByEmail(email).get()
		return Optional.ofNullable(get(userId))
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_user"
	}

}
