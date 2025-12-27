package io.zeitwert.fm.building.model

import dddrive.app.obj.model.ObjPart
import dddrive.ddd.property.model.PartListProperty
import io.dddrive.oe.model.ObjUser
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import java.time.LocalDate

interface ObjBuildingPartRating : ObjPart<ObjBuilding> {

	var ratingStatus: CodeBuildingRatingStatus?

	var ratingDate: LocalDate?

	val ratingYear: Int?

	var ratingUserId: Any?

	var ratingUser: ObjUser?

	var partCatalog: CodeBuildingPartCatalog?

	var maintenanceStrategy: CodeBuildingMaintenanceStrategy?

	val elementList: PartListProperty<ObjBuildingPartElementRating>

	fun getElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating

	fun addElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating

	val elementWeights: Int

	val condition: Int?

	fun getCondition(year: Int): Int?

}
