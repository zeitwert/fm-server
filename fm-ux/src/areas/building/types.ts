import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface BuildingElement {
	id: string;
	buildingPart?: Enumerated;
	weight?: number;
	condition?: number;
	conditionYear?: number;
	strain?: number;
	strength?: number;
	description?: string;
	conditionDescription?: string;
	measureDescription?: string;
	restorationYear?: number;
	restorationCosts?: number;
	lifeTime20?: number;
	lifeTime50?: number;
	lifeTime70?: number;
	lifeTime85?: number;
	lifeTime95?: number;
	lifeTime100?: number;
}

export interface BuildingRating {
	id: string;
	seqNr?: number;
	partCatalog?: Enumerated;
	maintenanceStrategy?: Enumerated;
	ratingStatus?: Enumerated;
	ratingDate?: string;
	ratingUser?: Enumerated;
	elements?: BuildingElement[];
}

export interface Building {
	id: string;
	meta?: EntityMeta;
	name: string;
	description?: string;
	buildingNr: string;
	insuranceNr?: string;
	plotNr?: string;
	nationalBuildingId?: string;
	historicPreservation?: Enumerated;
	buildingType?: Enumerated;
	buildingSubType?: Enumerated;
	buildingYear?: number;
	currency?: Enumerated;
	street?: string;
	zip?: string;
	city?: string;
	country?: Enumerated;
	geoAddress?: string;
	geoCoordinates?: string;
	geoZoom?: number;
	volume?: number;
	areaGross?: number;
	areaNet?: number;
	nrOfFloorsAboveGround?: number;
	nrOfFloorsBelowGround?: number;
	insuredValue: number;
	insuredValueYear: number;
	notInsuredValue?: number;
	notInsuredValueYear?: number;
	thirdPartyValue?: number;
	thirdPartyValueYear?: number;
	account?: Enumerated;
	tenant?: Enumerated;
	owner?: Enumerated;
	currentRating?: BuildingRating;
	contacts?: Enumerated[];
	coverFoto?: Enumerated;
}

export interface BuildingListItem {
	id: string;
	name: string;
	buildingNr?: string;
	city?: string;
	owner?: Enumerated;
	account?: Enumerated;
	currentRating?: {
		ratingStatus?: Enumerated;
	};
}

// Projection types for building evaluation
export interface ProjectionElement {
	element: Enumerated;
	building: Enumerated;
	buildingPart: Enumerated;
	restorationCosts: number;
}

export interface ProjectionPeriod {
	year: number;
	originalValue: number;
	timeValue: number;
	restorationCosts: number;
	restorationElements: ProjectionElement[];
	techPart: number;
	techRate: number;
	maintenanceRate: number;
	maintenanceCosts: number;
}

export interface ProjectionResult {
	startYear: number;
	duration: number;
	endYear: number;
	elementList: ProjectionElement[];
	periodList: ProjectionPeriod[];
}
