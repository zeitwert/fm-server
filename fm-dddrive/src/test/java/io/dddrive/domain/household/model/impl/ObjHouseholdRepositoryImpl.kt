package io.dddrive.domain.household.model.impl

import io.dddrive.domain.household.model.ObjHousehold
import io.dddrive.domain.household.model.ObjHouseholdPartMember
import io.dddrive.domain.household.model.ObjHouseholdRepository
import io.dddrive.obj.model.base.ObjRepositoryBase
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

	companion object {

		private const val AGGREGATE_TYPE = "objHousehold"
	}

	override fun createAggregate(isNew: Boolean): ObjHousehold = ObjHouseholdImpl(this, isNew)

	override val persistenceProvider get() = directory.getPersistenceProvider(ObjHousehold::class.java)

	override fun registerParts() {
		super.registerParts()
		this.addPart(ObjHouseholdPartMember::class.java, ::ObjHouseholdPartMemberImpl)
	}

}
