package io.zeitwert.fm.building.model.impl

import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.app.model.SessionContextFM
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.stereotype.Component

@Component("objBuildingRepository")
class ObjBuildingRepositoryImpl(
	override val accountRepository: ObjAccountRepository,
	override val contactRepository: ObjContactRepository,
	override val documentRepository: ObjDocumentRepository,
	override val taskRepository: DocTaskRepository,
	override val requestCtx: SessionContextFM,
) : FMObjRepositoryBase<ObjBuilding>(
		ObjBuilding::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjBuildingRepository {

	override fun createAggregate(isNew: Boolean): ObjBuilding = ObjBuildingImpl(this, isNew)

	override fun registerParts() {
		this.addPart(ObjBuildingPartRating::class.java, ::ObjBuildingPartRatingImpl)
		this.addPart(ObjBuildingPartElementRating::class.java, ::ObjBuildingPartElementRatingImpl)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_building"
	}

}
