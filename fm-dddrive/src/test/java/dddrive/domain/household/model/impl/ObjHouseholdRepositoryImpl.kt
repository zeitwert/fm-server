package dddrive.domain.household.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.obj.model.base.ObjRepositoryBase
import dddrive.domain.household.model.ObjHousehold
import dddrive.domain.household.model.ObjHouseholdPartMember
import dddrive.domain.household.model.ObjHouseholdRepository
import dddrive.domain.obj.persist.base.MapObjPersistenceProviderBase
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component("objHouseholdRepository")
class ObjHouseholdRepositoryImpl(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : ObjRepositoryBase<ObjHousehold>(
		ObjHousehold::class.java,
		AGGREGATE_TYPE,
	),
	ObjHouseholdRepository {

	override val sessionContext: SessionContext get() = sessionContextProvider.getObject()

	override val persistenceProvider = object : MapObjPersistenceProviderBase<ObjHousehold>(ObjHousehold::class.java) {}

	override fun createAggregate(isNew: Boolean): ObjHousehold = ObjHouseholdImpl(this, isNew)

	override fun registerParts() {
		super.registerParts()
		this.addPart(ObjHouseholdPartMember::class.java, ::ObjHouseholdPartMemberImpl)
	}

	companion object {

		private const val AGGREGATE_TYPE = "objHousehold"
	}

}
