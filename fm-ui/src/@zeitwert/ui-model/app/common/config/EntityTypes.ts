
export enum EntityType {
	ACCOUNT = "account",
	BUILDING = "building",
	CONTACT = "contact",
	DOCUMENT = "document",
	NOTE = "note",
	PORTFOLIO = "portfolio",
	TASK = "task",
	TENANT = "tenant",
	TEST = "test",
	USER = "user",
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
	hasPreview: boolean;
}

export const EntityTypes: { [type: string]: EntityTypeInfo } = {
	[EntityType.ACCOUNT]: {
		label: "Kunden",
		labelSingular: "Kunde",
		gender: EntityGender.MALE,
		type: EntityType.ACCOUNT,
		iconCategory: "standard",
		iconName: "account",
		hasPreview: false
	},
	[EntityType.BUILDING]: {
		label: "Immobilien",
		labelSingular: "Immobilie",
		gender: EntityGender.FEMALE,
		type: EntityType.BUILDING,
		iconCategory: "custom",
		iconName: "custom24",
		hasPreview: true
	},
	[EntityType.CONTACT]: {
		label: "Kontakte",
		labelSingular: "Kontakt",
		gender: EntityGender.MALE,
		type: EntityType.CONTACT,
		iconCategory: "standard",
		iconName: "contact",
		hasPreview: false
	},
	[EntityType.DOCUMENT]: {
		label: "Dokumente",
		labelSingular: "Dokument",
		gender: EntityGender.NEUTER,
		type: EntityType.DOCUMENT,
		iconCategory: "standard",
		iconName: "document",
		hasPreview: false
	},
	[EntityType.NOTE]: {
		label: "Notizen",
		labelSingular: "Notiz",
		gender: EntityGender.FEMALE,
		type: EntityType.NOTE,
		iconCategory: "standard",
		iconName: "note",
		hasPreview: false
	},
	[EntityType.PORTFOLIO]: {
		label: "Portfolios",
		labelSingular: "Portfolio",
		gender: EntityGender.NEUTER,
		type: EntityType.PORTFOLIO,
		iconCategory: "standard",
		iconName: "store_group",
		hasPreview: false
	},
	[EntityType.TASK]: {
		label: "Aufgaben",
		labelSingular: "Aufgabe",
		gender: EntityGender.FEMALE,
		type: EntityType.TASK,
		iconCategory: "standard",
		iconName: "task",
		hasPreview: false
	},
	[EntityType.TEST]: {
		label: "Tests",
		labelSingular: "Test",
		gender: EntityGender.MALE,
		type: EntityType.TEST,
		iconCategory: "standard",
		iconName: "task2",
		hasPreview: false
	},
	[EntityType.TENANT]: {
		label: "Mandanten",
		labelSingular: "Mandant",
		gender: EntityGender.MALE,
		type: EntityType.TENANT,
		iconCategory: "standard",
		iconName: "employee_organization",
		hasPreview: false
	},
	[EntityType.USER]: {
		label: "Benutzer",
		labelSingular: "Benutzer",
		gender: EntityGender.MALE,
		type: EntityType.USER,
		iconCategory: "standard",
		iconName: "user",
		hasPreview: false
	},
};
