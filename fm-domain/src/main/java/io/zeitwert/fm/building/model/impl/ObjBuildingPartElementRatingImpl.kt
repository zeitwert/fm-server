package io.zeitwert.fm.building.model.impl

import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.model.Part
import dddrive.ddd.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.model.Property
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

	override var buildingPart by enumProperty<CodeBuildingPart>("buildingPart")
	override var weight by baseProperty<Int>("weight")
	override var condition by baseProperty<Int>("condition")
	override var ratingYear by baseProperty<Int>("ratingYear")
	override var strain by baseProperty<Int>("strain")
	override var strength by baseProperty<Int>("strength")
	override var description by baseProperty<String>("description")
	override var conditionDescription by baseProperty<String>("conditionDescription")
	override var measureDescription by baseProperty<String>("measureDescription")

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
