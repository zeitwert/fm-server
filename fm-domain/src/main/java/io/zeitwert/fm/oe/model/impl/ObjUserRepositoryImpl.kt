package io.zeitwert.fm.oe.model.impl

import io.zeitwert.fm.app.model.SessionContextFM
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import io.zeitwert.fm.oe.persist.ObjUserSqlPersistenceProviderImpl
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
class ObjUserRepositoryImpl(
	@param:Lazy override val passwordEncoder: PasswordEncoder,
	@param:Lazy override val documentRepository: ObjDocumentRepository,
	override val requestCtx: SessionContextFM,
) : FMObjRepositoryBase<ObjUser>(
		ObjUser::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjUserRepository {

	override fun createAggregate(isNew: Boolean): ObjUser = ObjUserImpl(this, isNew)

	override fun isAppAdmin(user: ObjUser): Boolean = user.hasRole(CodeUserRole.APP_ADMIN)

	override fun isAdmin(user: ObjUser): Boolean = user.hasRole(CodeUserRole.ADMIN)

	override fun getByEmail(email: String): Optional<ObjUser> {
		val userId = (persistenceProvider as ObjUserSqlPersistenceProviderImpl).getByEmail(email)
		return userId.map { id -> get(id) }
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_user"
	}

}
