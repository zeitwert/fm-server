
import { EnumeratedField, IdField, NumberField, OptionField, TextField } from "@zeitwert/ui-forms";
import { BuildingElementModelType, StrainOptions, StrengthOptions } from "@zeitwert/ui-model";
import { converters, FormDefinition } from "mstform";

const BuildingElementFormDef: FormDefinition<BuildingElementModelType> = {
  id: new IdField(),
  buildingPart: new EnumeratedField({ source: "building/codeBuildingPart" }),
  weight: new NumberField(),
  condition: new NumberField(),
  strain: new OptionField(converters.maybe(converters.integer), { options: StrainOptions }),
  strength: new OptionField(converters.maybe(converters.integer), { options: StrengthOptions }),
  description: new TextField(),
  conditionDescription: new TextField(),
  measureDescription: new TextField(),
  // materialDescriptions: new EnumeratedListField(),
  // conditionDescriptions: new EnumeratedListField(),
  // measureDescriptions: new EnumeratedListField(),
};

export default BuildingElementFormDef;
