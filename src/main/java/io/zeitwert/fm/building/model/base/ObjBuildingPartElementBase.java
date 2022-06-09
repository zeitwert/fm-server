package io.zeitwert.fm.building.model.base;

import io.zeitwert.ddd.obj.model.base.ObjPartBase;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescription;
import io.zeitwert.fm.building.model.enums.CodeBuildingElementDescriptionEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;

import org.jooq.UpdatableRecord;

public abstract class ObjBuildingPartElementBase extends ObjPartBase<ObjBuilding> implements ObjBuildingPartElement {

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

	public ObjBuildingPartElementBase(ObjBuilding obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
		this.buildingPart = this.addEnumProperty(dbRecord, ObjBuildingPartElementFields.BUILDING_PART_ID,
				CodeBuildingPartEnum.class);
		this.valuePart = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.VALUE_PART);
		this.condition = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.CONDITION);
		this.conditionYear = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.CONDITION_YEAR);
		this.strain = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.STRAIN);
		this.strength = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.STRENGTH);
		this.description = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.DESCRIPTION);
		this.conditionDescription = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.CONDITION_DESCRIPTION);
		this.measureDescription = this.addSimpleProperty(dbRecord, ObjBuildingPartElementFields.MEASURE_DESCRIPTION);
		this.materialDescriptionSet = this.addEnumSetProperty(obj.getRepository().getMaterialDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
		this.conditionDescriptionSet = this.addEnumSetProperty(obj.getRepository().getConditionDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
		this.measureDescriptionSet = this.addEnumSetProperty(obj.getRepository().getMeasureDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
	}

}
