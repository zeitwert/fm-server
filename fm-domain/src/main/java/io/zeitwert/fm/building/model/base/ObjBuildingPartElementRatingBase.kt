package io.zeitwert.fm.building.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.model.Property
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.enums.CodeBuildingPart

abstract class ObjBuildingPartElementRatingBase(
	obj: ObjBuilding,
	repository: PartRepository<ObjBuilding, out Part<ObjBuilding>>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjBuilding>(obj, repository, property, id),
	ObjBuildingPartElementRating {

	override fun doInit() {
		super.doInit()
		addEnumProperty("buildingPart", CodeBuildingPart::class.java)
		addBaseProperty("weight", Int::class.java)
		addBaseProperty("condition", Int::class.java)
		addBaseProperty("ratingYear", Int::class.java)
		addBaseProperty("strain", Int::class.java)
		addBaseProperty("strength", Int::class.java)
		addBaseProperty("description", String::class.java)
		addBaseProperty("conditionDescription", String::class.java)
		addBaseProperty("measureDescription", String::class.java)
	}

	// protected final EnumSetProperty<CodeBuildingElementDescription> materialDescriptionSet;
	// protected final EnumSetProperty<CodeBuildingElementDescription> conditionDescriptionSet;
	// protected final EnumSetProperty<CodeBuildingElementDescription> measureDescriptionSet;

	// @Override
	// public Integer getRatingYear() {
	// return this.ratingYear.getValue() != null ? this.ratingYear.getValue() :
	// this.getParent().getRatingYear();
	// }

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
