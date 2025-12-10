import { Enumerated } from "../../../../ui-model/ddd/aggregate/model/EnumeratedModel";

export interface RestorationElement {
	building: Enumerated;
	buildingPart: Enumerated;
	element: Enumerated;
	restorationCosts: number;
}

export interface ProjectionPeriod {
	year: number;
	originalValue: number;
	timeValue: number;
	restorationCosts: number;
	restorationElements: RestorationElement[];
	techPart: number;
	techRate: number;
	maintenanceRate: number;
	maintenanceCosts: number;
}

export interface ProjectionResult {
	startYear: number;
	duration: number;
	endYear: number;
	elementList: RestorationElement[];
	periodList: ProjectionPeriod[];
}

export const EMPTY_RESULT = {
	startYear: 0,
	duration: 0,
	endYear: 0,
	periodList: [],
	elementList: []
	//Map<EnumeratedDto, List<ProjectionPeriod>> partResultMap;
};
