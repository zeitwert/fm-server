package io.zeitwert.fm.building.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescription;
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescriptionEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;

public abstract class ObjBuildingPartElementRatingBase extends ObjPartBase<ObjBuilding>
		implements ObjBuildingPartElementRating {

	protected final EnumProperty<CodeBuildingPart> buildingPart;
	protected final SimpleProperty<Integer> valuePart;
	protected final SimpleProperty<Integer> condition;
	protected final SimpleProperty<Integer> conditionYear;
	protected final SimpleProperty<Integer> strain;
	protected final SimpleProperty<Integer> strength;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<String> conditionDescription;
	protected final SimpleProperty<String> measureDescription;
	protected final EnumSetProperty<CodeBuildingElementDescription> materialDescriptionSet;
	protected final EnumSetProperty<CodeBuildingElementDescription> conditionDescriptionSet;
	protected final EnumSetProperty<CodeBuildingElementDescription> measureDescriptionSet;

	public ObjBuildingPartElementRatingBase(PartRepository<ObjBuilding, ?> repository, ObjBuilding obj,
			UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
		ObjBuildingRepository repo = (ObjBuildingRepository) obj.getMeta().getRepository();
		this.buildingPart = this.addEnumProperty(dbRecord, ObjBuildingPartElementRatingFields.BUILDING_PART_ID,
				CodeBuildingPartEnum.class);
		this.valuePart = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.VALUE_PART);
		this.condition = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.CONDITION);
		this.conditionYear = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.CONDITION_YEAR);
		this.strain = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.STRAIN);
		this.strength = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.STRENGTH);
		this.description = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.DESCRIPTION);
		this.conditionDescription = this.addSimpleProperty(dbRecord,
				ObjBuildingPartElementRatingFields.CONDITION_DESCRIPTION);
		this.measureDescription = this.addSimpleProperty(dbRecord, ObjBuildingPartElementRatingFields.MEASURE_DESCRIPTION);
		this.materialDescriptionSet = this.addEnumSetProperty(repo.getMaterialDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
		this.conditionDescriptionSet = this.addEnumSetProperty(repo.getConditionDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
		this.measureDescriptionSet = this.addEnumSetProperty(repo.getMeasureDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
	}

}
