
import { EnumeratedField, IdField, IntField, NumberField, TextField } from "@zeitwert/ui-forms";
import { BuildingModel, Config, Enumerated } from "@zeitwert/ui-model";
import axios from "axios";
import { Form, Query } from "mstform";

const loadBuildingSubTypes = async (q: Query): Promise<Enumerated[]> => {
	if (q.buildingType?.id) {
		const subTypesResponse = await axios.get(Config.getEnumUrl("building", "codeBuildingSubType/" + q.buildingType.id));
		if (subTypesResponse) {
			return subTypesResponse.data;
		}
	}
	return [];
};

const BuildingFormModel = new Form(
	BuildingModel,
	{
		id: new IdField(),
		name: new TextField({ required: true }),
		description: new TextField(),
		owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
		//
		buildingNr: new TextField({ required: true }),
		insuranceNr: new TextField(),
		plotNr: new TextField(),
		nationalBuildingId: new TextField(),
		historicPreservation: new EnumeratedField({ source: "building/codeHistoricPreservation" }),
		//
		street: new TextField(),
		zip: new TextField(),
		city: new TextField(),
		country: new EnumeratedField({ source: "oe/codeCountry" }),
		//
		buildingYear: new IntField({ minValue: 1000, maxLength: 4 }),
		volume: new NumberField(),
		areaGross: new NumberField(),
		areaNet: new NumberField(),
		nrOfFloorsAboveGround: new NumberField(),
		nrOfFloorsBelowGround: new NumberField(),
		//
		currency: new EnumeratedField({ source: "account/codeCurrency" }),
		insuredValue: new NumberField({ required: true }),
		insuredValueYear: new IntField({ required: true, minValue: 1800, maxLength: 4 }),
		notInsuredValue: new NumberField(),
		notInsuredValueYear: new IntField({ minValue: 1800, maxLength: 4 }),
		thirdPartyValue: new NumberField(),
		thirdPartyValueYear: new IntField({ minValue: 1800, maxLength: 4 }),
		//
		buildingType: new EnumeratedField({ source: "building/codeBuildingType" }),
		buildingSubType: new EnumeratedField({
			source: loadBuildingSubTypes,
			dependentQuery: (accessor) => {
				return { buildingType: accessor.node.buildingType };
			}
		}),
	}
);

export default BuildingFormModel;
