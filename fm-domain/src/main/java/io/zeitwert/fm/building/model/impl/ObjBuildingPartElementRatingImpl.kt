package io.zeitwert.fm.building.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.model.Property
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.enums.CodeBuildingPart

class ObjBuildingPartElementRatingImpl(
	obj: ObjBuilding,
	repository: PartRepository<ObjBuilding, out Part<ObjBuilding>>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjBuilding>(obj, repository, property, id),
	ObjBuildingPartElementRating {

	override var buildingPart: CodeBuildingPart? by enumProperty(this, "buildingPart")
	override var weight: Int? by baseProperty(this, "weight")
	override var condition: Int? by baseProperty(this, "condition")
	override var ratingYear: Int? by baseProperty(this, "ratingYear")
	override var strain: Int? by baseProperty(this, "strain")
	override var strength: Int? by baseProperty(this, "strength")
	override var description: String? by baseProperty(this, "description")
	override var conditionDescription: String? by baseProperty(this, "conditionDescription")
	override var measureDescription: String? by baseProperty(this, "measureDescription")

	override fun getCondition(year: Int): Int {
		val buildingPart = this.buildingPart!!
		try {
			val relativeAgeAtRating = buildingPart.getRelativeAge(this.condition!! / 100.0)
			val relativeAgeAtYear = relativeAgeAtRating + (year - this.ratingYear!!)
			return Math.round(buildingPart.getTimeValue(relativeAgeAtYear) * 100.0).toInt()
		} catch (e: Exception) {
			throw RuntimeException(
				this.aggregate.name + "." + buildingPart.name + ".getCondition(" + year + ")",
				e,
			)
		}
	}

}
