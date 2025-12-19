package io.zeitwert.fm.building.model

import io.dddrive.core.obj.model.ObjPart
import io.dddrive.core.oe.model.ObjUser
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import java.time.LocalDate

interface ObjBuildingPartRating : ObjPart<ObjBuilding> {

	var ratingStatus: CodeBuildingRatingStatus?

	var ratingDate: LocalDate?

	val ratingYear: Int?

	var ratingUser: ObjUser?

	var partCatalog: CodeBuildingPartCatalog?

	var maintenanceStrategy: CodeBuildingMaintenanceStrategy?

	val elementCount: Int

	fun getElement(seqNr: Int): ObjBuildingPartElementRating

	val elementList: List<ObjBuildingPartElementRating>

	fun getElementById(elementId: Int): ObjBuildingPartElementRating

	fun getElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating

	fun clearElementList()

	fun addElement(buildingPart: CodeBuildingPart): ObjBuildingPartElementRating

	fun removeElement(elementId: Int)

	val elementWeights: Int

	val condition: Int?

	fun getCondition(year: Int): Int?

}
