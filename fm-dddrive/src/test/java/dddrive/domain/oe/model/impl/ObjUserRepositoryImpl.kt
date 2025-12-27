package dddrive.domain.oe.model.impl

import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.oe.model.ObjUserRepository
import dddrive.domain.oe.persist.ObjUserPersistenceProvider
import io.dddrive.oe.model.ObjUser
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserRepository")
@DependsOn("objUserPersistenceProvider")
class ObjUserRepositoryImpl :
	ObjRepositoryBase<ObjUser>(
		ObjUser::class.java,
		AGGREGATE_TYPE,
	),
	ObjUserRepository {

	override fun createAggregate(isNew: Boolean): ObjUser = ObjUserTestImpl(this, isNew)

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjUser::class.java) as ObjUserPersistenceProvider

	override fun getByEmail(email: String): Optional<ObjUser> = this.persistenceProvider.getByEmail(email).map { get(it) }

	companion object {

		private const val AGGREGATE_TYPE = "objUser"
	}

}
