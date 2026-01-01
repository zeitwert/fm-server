
import { DateField, EnumeratedField, IdField, IntField, NumberField, TextField } from "@zeitwert/ui-forms";
import { API, BuildingModel, BuildingModelType, Config, Enumerated } from "@zeitwert/ui-model";
import { isAlive } from "mobx-state-tree";
import { Form, FormDefinition, Query, RepeatingForm, SubForm } from "mstform";
import BuildingElementFormDef from "./BuildingElementFormDef";

const loadBuildingSubTypes = async (q?: Query): Promise<Enumerated[]> => {
	if (!q?.buildingTypeId) {
		return [];
	}
	const subTypesResponse = await API.get(Config.getEnumUrl("building", "codeBuildingSubType/" + q.buildingTypeId));
	return subTypesResponse.data ?? [];
};

export const BuildingFormDef: FormDefinition<BuildingModelType> = {
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
	geoAddress: new TextField(),
	geoCoordinates: new TextField(),
	geoZoom: new NumberField(),
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
			return isAlive(accessor.node) && !!accessor.node.buildingType?.id
				? { buildingTypeId: accessor.node.buildingType.id }
				: [];
		}
	}),
	//
	currentRating: new SubForm({
		partCatalog: new EnumeratedField({ required: true, source: "building/codeBuildingPartCatalog" }),
		maintenanceStrategy: new EnumeratedField({ required: true, source: "building/codeBuildingMaintenanceStrategy" }),
		ratingStatus: new EnumeratedField({ source: "building/codeBuildingRatingStatus" }),
		ratingDate: new DateField({ required: true }),
		ratingUser: new EnumeratedField({ source: "oe/objUser" }),
		elements: new RepeatingForm(BuildingElementFormDef)
	})
};

const BuildingForm = new Form(BuildingModel, BuildingFormDef);

export default BuildingForm;
