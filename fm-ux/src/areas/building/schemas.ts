import { z } from "zod";
import type { Enumerated } from "../../common/types";
import type { BuildingRating } from "./types";
import { displayOnly, enumeratedSchema } from "../../common/utils/zodMeta";

export interface BuildingCreationFormInput {
	name: string;
	buildingNr: string;
	owner: Enumerated | null;
	account: Enumerated | null;
	insuredValue: number;
	insuredValueYear: number;
	street?: string;
	zip?: string;
	city?: string;
	country: Enumerated | null;
}

export const buildingCreationSchema = z.object({
	name: z.string().min(1, "building:message.validation.nameRequired"),
	buildingNr: z.string().min(1, "building:message.validation.buildingNrRequired"),
	owner: enumeratedSchema,
	account: enumeratedSchema,
	insuredValue: z.number().min(0, "building:message.validation.insuredValueRequired"),
	insuredValueYear: z
		.number()
		.min(1800, "building:message.validation.insuredValueYearMin")
		.max(2100, "building:message.validation.insuredValueYearMax"),
	street: z.string().optional(),
	zip: z.string().optional(),
	city: z.string().optional(),
	country: enumeratedSchema.optional().nullable(),
});

export type BuildingCreationData = z.infer<typeof buildingCreationSchema>;

export interface BuildingElementFormInput {
	id: string;
	buildingPart?: Enumerated;
	weight?: number | null;
	condition?: number | null;
	description?: string | null;
	conditionDescription?: string | null;
	measureDescription?: string | null;
}

export interface BuildingRatingFormInput {
	partCatalog: Enumerated | null;
	maintenanceStrategy: Enumerated | null;
	ratingStatus: Enumerated | null;
	ratingDate: string | null;
	ratingUser: Enumerated | null;
	elements: BuildingElementFormInput[];
}

export interface BuildingFormInput {
	name: string;
	description?: string | null;
	buildingNr: string;
	insuranceNr?: string | null;
	plotNr?: string | null;
	nationalBuildingId?: string | null;
	historicPreservation?: Enumerated | null;
	buildingType?: Enumerated | null;
	buildingSubType?: Enumerated | null;
	buildingYear?: number | null;
	currency?: Enumerated | null;
	street?: string | null;
	zip?: string | null;
	city?: string | null;
	country?: Enumerated | null;
	geoAddress?: string | null;
	geoCoordinates?: string | null;
	geoZoom?: number | null;
	volume?: number | null;
	areaGross?: number | null;
	areaNet?: number | null;
	nrOfFloorsAboveGround?: number | null;
	nrOfFloorsBelowGround?: number | null;
	insuredValue: number;
	insuredValueYear: number;
	notInsuredValue?: number | null;
	notInsuredValueYear?: number | null;
	thirdPartyValue?: number | null;
	thirdPartyValueYear?: number | null;
	owner: Enumerated | null;
	currentRating?: BuildingRating;
	contacts?: Enumerated[];
}

const optionalEnumerated = z
	.object({
		id: z.string(),
		name: z.string(),
	})
	.optional();

const elementSchema = z.object({
	id: z.string(),
	buildingPart: optionalEnumerated,
	weight: z.number().optional(),
	condition: z.number().min(0).max(100).optional(),
	description: z.string().optional(),
	conditionDescription: z.string().optional(),
	measureDescription: z.string().optional(),
});

const ratingSchema = z.object({
	id: z.string(),
	seqNr: z.number().optional(),
	partCatalog: optionalEnumerated,
	maintenanceStrategy: optionalEnumerated,
	ratingStatus: optionalEnumerated,
	ratingDate: z.string().optional(),
	ratingUser: optionalEnumerated,
	elements: z.array(elementSchema),
});

export const buildingFormSchema = z.object({
	name: z.string().min(1, "building:message.validation.nameRequired"),
	description: z.string().optional().nullable(),
	buildingNr: z.string().min(1, "building:message.validation.buildingNrRequired"),
	insuranceNr: z.string().optional().nullable(),
	plotNr: z.string().optional().nullable(),
	nationalBuildingId: z.string().optional().nullable(),
	historicPreservation: enumeratedSchema.optional().nullable(),
	buildingType: enumeratedSchema.optional().nullable(),
	buildingSubType: enumeratedSchema.optional().nullable(),
	buildingYear: z.number().min(1000).max(2100).optional().nullable(),
	currency: enumeratedSchema.optional().nullable(),
	street: z.string().optional().nullable(),
	zip: z.string().optional().nullable(),
	city: z.string().optional().nullable(),
	country: enumeratedSchema.optional().nullable(),
	geoAddress: z.string().optional().nullable(),
	geoCoordinates: z.string().optional().nullable(),
	geoZoom: z.number().optional().nullable(),
	volume: z.number().optional().nullable(),
	areaGross: z.number().optional().nullable(),
	areaNet: z.number().optional().nullable(),
	nrOfFloorsAboveGround: z.number().optional().nullable(),
	nrOfFloorsBelowGround: z.number().optional().nullable(),
	insuredValue: z.number().min(0, "building:message.validation.insuredValueRequired"),
	insuredValueYear: z
		.number()
		.min(1800, "building:message.validation.insuredValueYearMin")
		.max(2100, "building:message.validation.insuredValueYearMax"),
	notInsuredValue: z.number().optional().nullable(),
	notInsuredValueYear: z.number().min(1800).max(2100).optional().nullable(),
	thirdPartyValue: z.number().optional().nullable(),
	thirdPartyValueYear: z.number().min(1800).max(2100).optional().nullable(),
	owner: enumeratedSchema,
	currentRating: displayOnly(ratingSchema.optional()),
	contacts: displayOnly(z.array(enumeratedSchema).optional()),
});

export type BuildingFormData = z.infer<typeof buildingFormSchema>;
