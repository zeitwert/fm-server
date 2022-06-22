
export enum EntityType {
	PORTFOLIO = "portfolio",
	ACCOUNT = "account",
	CONTACT = "contact",
	DOCUMENT = "document",
	BUILDING = "building",
	LIFE_EVENT = "lifeEvent",
	TASK = "task",
	NOTE = "note",
	LEAD = "lead"
}

export enum EntityGender {
	MALE = "male",
	FEMALE = "female",
	NEUTER = "neuter",
}

export type IconCategory = "standard" | "action" | "custom" | "doctype" | "utility";

export interface EntityTypeInfo {
	type: EntityType;
	label: string;
	labelSingular: string;
	gender: EntityGender;
	iconCategory: IconCategory;
	iconName: string;
	isFavoritable: boolean;
	hasPreview: boolean;
}

export const EntityTypes: { [type: string]: EntityTypeInfo } = {
	[EntityType.PORTFOLIO]: {
		label: "Portfolios",
		labelSingular: "Portfolio",
		gender: EntityGender.NEUTER,
		type: EntityType.PORTFOLIO,
		iconCategory: "standard",
		iconName: "store_group",
		isFavoritable: true,
		hasPreview: false
	},
	[EntityType.ACCOUNT]: {
		label: "Kunden",
		labelSingular: "Kunde",
		gender: EntityGender.MALE,
		type: EntityType.ACCOUNT,
		iconCategory: "standard",
		iconName: "account",
		isFavoritable: true,
		hasPreview: false
	},
	[EntityType.CONTACT]: {
		label: "Kontakte",
		labelSingular: "Kontakt",
		gender: EntityGender.MALE,
		type: EntityType.CONTACT,
		iconCategory: "standard",
		iconName: "contact",
		isFavoritable: true,
		hasPreview: false
	},
	[EntityType.DOCUMENT]: {
		label: "Dokumente",
		labelSingular: "Dokument",
		gender: EntityGender.NEUTER,
		type: EntityType.DOCUMENT,
		iconCategory: "standard",
		iconName: "document",
		isFavoritable: false,
		hasPreview: false
	},
	[EntityType.BUILDING]: {
		label: "Immobilien",
		labelSingular: "Immobilie",
		gender: EntityGender.FEMALE,
		type: EntityType.BUILDING,
		iconCategory: "custom",
		iconName: "custom24",
		isFavoritable: true,
		hasPreview: true
	},
	[EntityType.TASK]: {
		label: "Aufgaben",
		labelSingular: "Aufgabe",
		gender: EntityGender.FEMALE,
		type: EntityType.TASK,
		iconCategory: "standard",
		iconName: "task",
		isFavoritable: false,
		hasPreview: false
	},
	[EntityType.NOTE]: {
		label: "Notizen",
		labelSingular: "Notiz",
		gender: EntityGender.FEMALE,
		type: EntityType.NOTE,
		iconCategory: "standard",
		iconName: "note",
		isFavoritable: false,
		hasPreview: false
	},
	[EntityType.LEAD]: {
		label: "Leads",
		labelSingular: "Lead",
		gender: EntityGender.MALE,
		type: EntityType.LEAD,
		iconCategory: "standard",
		iconName: "lead",
		isFavoritable: true,
		hasPreview: false
	}
};
