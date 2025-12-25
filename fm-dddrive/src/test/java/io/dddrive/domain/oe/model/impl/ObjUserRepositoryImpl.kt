package io.dddrive.domain.oe.model.impl

import io.dddrive.domain.oe.model.ObjUserRepository
import io.dddrive.domain.oe.persist.ObjUserPersistenceProvider
import io.dddrive.obj.model.base.ObjRepositoryBase
import io.dddrive.oe.model.ObjUser
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
@DependsOn("objUserPersistenceProvider")
class ObjUserRepositoryImpl :
	ObjRepositoryBase<ObjUser>(
		ObjUserRepository::class.java,
		ObjUser::class.java,
		ObjUserTestImpl::class.java,
		AGGREGATE_TYPE,
	),
	ObjUserRepository {

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjUser::class.java) as ObjUserPersistenceProvider

	override fun getByEmail(email: String): Optional<ObjUser> = this.persistenceProvider.getByEmail(email).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objUser"
	}

}
