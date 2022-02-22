package fm.comunas.fm.building.model.base;

import fm.comunas.ddd.obj.model.base.ObjPartBase;
import fm.comunas.ddd.property.model.EnumProperty;
import fm.comunas.ddd.property.model.EnumSetProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.ObjBuildingPartElement;
import fm.comunas.fm.building.model.enums.CodeBuildingElementDescription;
import fm.comunas.fm.building.model.enums.CodeBuildingElementDescriptionEnum;
import fm.comunas.fm.building.model.enums.CodeBuildingPart;
import fm.comunas.fm.building.model.enums.CodeBuildingPartEnum;

import org.jooq.UpdatableRecord;

public abstract class ObjBuildingPartElementBase extends ObjPartBase<ObjBuilding> implements ObjBuildingPartElement {

	protected final EnumProperty<CodeBuildingPart> buildingPart;
	protected final SimpleProperty<Integer> valuePart;
	protected final SimpleProperty<Integer> condition;
	protected final SimpleProperty<Integer> conditionYear;
	protected final SimpleProperty<Integer> strain;
	protected final SimpleProperty<Integer> strength;
	protected final SimpleProperty<String> description;
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
		this.materialDescriptionSet = this.addEnumSetProperty(obj.getRepository().getMaterialDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
		this.conditionDescriptionSet = this.addEnumSetProperty(obj.getRepository().getConditionDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
		this.measureDescriptionSet = this.addEnumSetProperty(obj.getRepository().getMeasureDescriptionSetType(),
				CodeBuildingElementDescriptionEnum.class);
	}

}
