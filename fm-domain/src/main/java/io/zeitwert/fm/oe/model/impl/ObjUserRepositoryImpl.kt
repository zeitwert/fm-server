package io.zeitwert.fm.oe.model.impl

import io.zeitwert.app.obj.model.base.FMObjRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.persist.ObjUserPersistenceProvider
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
@DependsOn("objUserPersistenceProvider")
class ObjUserRepositoryImpl(
	@param:Lazy override val documentRepository: ObjDocumentRepository,
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjUser>(
		ObjUser::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjUserRepository {

	override val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

	override fun createAggregate(isNew: Boolean): ObjUser = ObjUserImpl(this, isNew)

	override fun getByEmail(email: String): Optional<ObjUser> {
		val userId = (persistenceProvider as ObjUserPersistenceProvider).getByEmail(email)
		return userId.map { id -> get(id) }
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_user"
	}

}
