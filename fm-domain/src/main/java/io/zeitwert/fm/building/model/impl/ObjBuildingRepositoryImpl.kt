package io.zeitwert.fm.building.model.impl

import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.base.ObjBuildingBase
import io.zeitwert.fm.building.model.base.ObjBuildingPartElementRatingBase
import io.zeitwert.fm.building.model.base.ObjBuildingPartRatingBase
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
) :
	FMObjRepositoryBase<ObjBuilding>(
		ObjBuildingRepository::class.java,
		ObjBuilding::class.java,
		ObjBuildingBase::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjBuildingRepository {

	override fun registerParts() {
		this.addPart(ObjBuilding::class.java, ObjBuildingPartRating::class.java, ObjBuildingPartRatingBase::class.java)
		this.addPart(
			ObjBuilding::class.java,
			ObjBuildingPartElementRating::class.java,
			ObjBuildingPartElementRatingBase::class.java,
		)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_building"
	}

}
