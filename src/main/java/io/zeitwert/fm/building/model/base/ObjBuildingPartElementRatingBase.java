package io.zeitwert.fm.building.model.base;

import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.jooq.persistence.PartState;

public abstract class ObjBuildingPartElementRatingBase extends ObjPartBase<ObjBuilding>
		implements ObjBuildingPartElementRating {

	//@formatter:off
	protected final EnumProperty<CodeBuildingPart> buildingPart = this.addEnumProperty("buildingPart", CodeBuildingPart.class);
	protected final SimpleProperty<Integer> weight = this.addSimpleProperty("weight", Integer.class);
	protected final SimpleProperty<Integer> condition = this.addSimpleProperty("condition", Integer.class);
	protected final SimpleProperty<Integer> ratingYear = this.addSimpleProperty("ratingYear", Integer.class);
	protected final SimpleProperty<Integer> strain = this.addSimpleProperty("strain", Integer.class);
	protected final SimpleProperty<Integer> strength = this.addSimpleProperty("strength", Integer.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	protected final SimpleProperty<String> conditionDescription = this.addSimpleProperty("conditionDescription", String.class);
	protected final SimpleProperty<String> measureDescription = this.addSimpleProperty("measureDescription", String.class);
	// protected final EnumSetProperty<CodeBuildingElementDescription> materialDescriptionSet;
	// protected final EnumSetProperty<CodeBuildingElementDescription> conditionDescriptionSet;
	// protected final EnumSetProperty<CodeBuildingElementDescription> measureDescriptionSet;
	//@formatter:on

	public ObjBuildingPartElementRatingBase(PartRepository<ObjBuilding, ?> repository, ObjBuilding obj, PartState state) {
		super(repository, obj, state);
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		// PartRepository<?, ObjPartItem> itemRepo =
		// AppContext.getInstance().getPartRepository(ObjPartItem.class);
		// if (property.equals(this.materialDescriptionSet)) {
		// return itemRepo.create(this, partListType);
		// } else if (property.equals(this.conditionDescriptionSet)) {
		// return itemRepo.create(this, partListType);
		// } else if (property.equals(this.measureDescriptionSet)) {
		// return itemRepo.create(this, partListType);
		// }
		return super.addPart(property, partListType);
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
