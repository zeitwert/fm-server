
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

export type IconCategory = "standard" | "action" | "custom" | "doctype" | "utility";

export interface EntityTypeInfo {
	type: EntityType;
	label: string;
	labelSingular: string;
	iconCategory: IconCategory;
	iconName: string;
	isFavoritable: boolean;
}

export const EntityTypes: { [type: string]: EntityTypeInfo } = {
	[EntityType.PORTFOLIO]: {
		label: "Portfolios",
		labelSingular: "Portfolio",
		type: EntityType.PORTFOLIO,
		iconCategory: "standard",
		iconName: "store_group",
		isFavoritable: true
	},
	[EntityType.ACCOUNT]: {
		label: "Kunden",
		labelSingular: "Kunde",
		type: EntityType.ACCOUNT,
		iconCategory: "standard",
		iconName: "account",
		isFavoritable: true
	},
	[EntityType.CONTACT]: {
		label: "Kontakte",
		labelSingular: "Kontakt",
		type: EntityType.CONTACT,
		iconCategory: "standard",
		iconName: "contact",
		isFavoritable: true
	},
	[EntityType.DOCUMENT]: {
		label: "Dokumente",
		labelSingular: "Dokument",
		type: EntityType.DOCUMENT,
		iconCategory: "standard",
		iconName: "document",
		isFavoritable: false
	},
	[EntityType.BUILDING]: {
		label: "Immobilien",
		labelSingular: "Immobilie",
		type: EntityType.BUILDING,
		iconCategory: "custom",
		iconName: "custom24",
		isFavoritable: true
	},
	[EntityType.LIFE_EVENT]: {
		label: "Life Events",
		labelSingular: "Life Event",
		type: EntityType.LIFE_EVENT,
		iconCategory: "standard",
		iconName: "key_dates",
		isFavoritable: true
	},
	[EntityType.TASK]: {
		label: "Aufgaben",
		labelSingular: "Aufgabe",
		type: EntityType.TASK,
		iconCategory: "standard",
		iconName: "task",
		isFavoritable: false
	},
	[EntityType.NOTE]: {
		label: "Notizen",
		labelSingular: "Notiz",
		type: EntityType.NOTE,
		iconCategory: "standard",
		iconName: "note",
		isFavoritable: false
	},
	[EntityType.LEAD]: {
		label: "Leads",
		labelSingular: "Lead",
		type: EntityType.LEAD,
		iconCategory: "standard",
		iconName: "lead",
		isFavoritable: true
	}
};
