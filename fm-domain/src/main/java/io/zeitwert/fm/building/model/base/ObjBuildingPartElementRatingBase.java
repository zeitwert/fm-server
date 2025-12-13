package io.zeitwert.fm.building.model.base;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.obj.model.base.ObjPartBase;
import io.dddrive.core.property.model.EnumProperty;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.Property;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;

public abstract class ObjBuildingPartElementRatingBase extends ObjPartBase<ObjBuilding>
		implements ObjBuildingPartElementRating {

	//@formatter:off
	protected final EnumProperty<CodeBuildingPart> buildingPart = this.addEnumProperty("buildingPart", CodeBuildingPart.class);
	protected final BaseProperty<Integer> weight = this.addBaseProperty("weight", Integer.class);
	protected final BaseProperty<Integer> condition = this.addBaseProperty("condition", Integer.class);
	protected final BaseProperty<Integer> ratingYear = this.addBaseProperty("ratingYear", Integer.class);
	protected final BaseProperty<Integer> strain = this.addBaseProperty("strain", Integer.class);
	protected final BaseProperty<Integer> strength = this.addBaseProperty("strength", Integer.class);
	protected final BaseProperty<String> description = this.addBaseProperty("description", String.class);
	protected final BaseProperty<String> conditionDescription = this.addBaseProperty("conditionDescription", String.class);
	protected final BaseProperty<String> measureDescription = this.addBaseProperty("measureDescription", String.class);
	// protected final EnumSetProperty<CodeBuildingElementDescription> materialDescriptionSet;
	// protected final EnumSetProperty<CodeBuildingElementDescription> conditionDescriptionSet;
	// protected final EnumSetProperty<CodeBuildingElementDescription> measureDescriptionSet;
	//@formatter:on

	protected ObjBuildingPartElementRatingBase(ObjBuilding obj, PartRepository<ObjBuilding, ? extends Part<ObjBuilding>> repository, Property<?> property, Integer id) {
		super(obj, repository, property, id);
	}

	// @Override
	// public Integer getRatingYear() {
	// return this.ratingYear.getValue() != null ? this.ratingYear.getValue() :
	// this.getParent().getRatingYear();
	// }

	@Override
	public Integer getCondition(Integer year) {
		try {
			CodeBuildingPart buildingPart = this.buildingPart.getValue();
			double relativeAgeAtRating = buildingPart.getRelativeAge(this.getCondition() / 100.0);
			double relativeAgeAtYear = relativeAgeAtRating + (year - this.getRatingYear());
			return (int) Math.round(buildingPart.getTimeValue(relativeAgeAtYear) * 100.0);
		} catch (Exception e) {
			throw new RuntimeException(
					this.getAggregate().getName() + "." + this.buildingPart.getValue().getName() + ".getCondition(" + year + ")",
					e);
		}
	}

}
