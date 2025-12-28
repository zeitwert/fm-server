package dddrive.domain.household.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.ddd.model.impl.SessionContextImpl
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.ObjHouseholdPartMember
import dddrive.domain.household.model.ObjHouseholdRepository
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

@Component("objHouseholdRepository")
@DependsOn("objHouseholdPersistenceProvider")
class ObjHouseholdRepositoryImpl :
	ObjRepositoryBase<ObjHousehold>(
		ObjHousehold::class.java,
		AGGREGATE_TYPE,
	),
	ObjHouseholdRepository {

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjHousehold::class.java)

	override lateinit var sessionContext: SessionContext

	fun initSessionContext(
		tenantId: Any,
		accountId: Any,
		userId: Any,
	) {
		sessionContext = SessionContextImpl(
			tenantId = tenantId,
			accountId = accountId,
			userId = userId,
		)
	}

	override fun createAggregate(isNew: Boolean): ObjHousehold = ObjHouseholdImpl(this, isNew)

	override fun registerParts() {
		super.registerParts()
		this.addPart(ObjHouseholdPartMember::class.java, ::ObjHouseholdPartMemberImpl)
	}

	companion object {

		private const val AGGREGATE_TYPE = "objHousehold"
	}

}
