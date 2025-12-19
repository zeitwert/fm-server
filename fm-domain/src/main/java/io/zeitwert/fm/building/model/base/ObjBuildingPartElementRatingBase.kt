package io.zeitwert.fm.building.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.base.ObjPartBase
import io.dddrive.core.property.model.Property
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

	protected val _buildingPart = addEnumProperty("buildingPart", CodeBuildingPart::class.java)
	protected val _weight = addBaseProperty("weight", Int::class.java)
	protected val _condition = addBaseProperty("condition", Int::class.java)
	protected val _ratingYear = addBaseProperty("ratingYear", Int::class.java)
	protected val _strain = addBaseProperty("strain", Int::class.java)
	protected val _strength = addBaseProperty("strength", Int::class.java)
	protected val _description = addBaseProperty("description", String::class.java)
	protected val _conditionDescription = addBaseProperty("conditionDescription", String::class.java)
	protected val _measureDescription = addBaseProperty("measureDescription", String::class.java)

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
