/**
 * Central registry mapping itemType IDs to metadata.
 * Used by AfItemSelect to render icons, labels, and sort order for entity references.
 */
import type { ReactNode } from "react";
import {
	AppstoreOutlined,
	BankOutlined,
	CheckSquareOutlined,
	FileOutlined,
	FileTextOutlined,
	FormOutlined,
	HomeOutlined,
	TeamOutlined,
	UserOutlined,
} from "@ant-design/icons";
import React from "react";

export interface ItemTypeMeta {
	id: string; // Normalized ID (e.g., "account")
	aggregateType: string; // Server aggregate type (e.g., "obj_account", "doc_task")
	icon: ReactNode;
	singularKey: string; // i18n key, e.g., "account:label.entity"
	pluralKey: string; // i18n key, e.g., "app:label.accounts"
	path: string; // Route segment, e.g., "account"
	sortOrder: number; // For ordering (lower = more general)
}

// Registry entries - sortOrder determines display order in selects
const itemTypes: ItemTypeMeta[] = [
	{
		id: "account",
		aggregateType: "obj_account",
		icon: React.createElement(BankOutlined),
		singularKey: "account:label.entity",
		pluralKey: "app:label.accounts",
		path: "account",
		sortOrder: 10,
	},
	{
		id: "building",
		aggregateType: "obj_building",
		icon: React.createElement(HomeOutlined),
		singularKey: "building:label.entity",
		pluralKey: "app:label.buildings",
		path: "building",
		sortOrder: 30,
	},
	{
		id: "contact",
		aggregateType: "obj_contact",
		icon: React.createElement(TeamOutlined),
		singularKey: "contact:label.entity",
		pluralKey: "app:label.contacts",
		path: "contact",
		sortOrder: 40,
	},
	{
		id: "document",
		aggregateType: "obj_document",
		icon: React.createElement(FileTextOutlined),
		singularKey: "document:label.entity",
		pluralKey: "app:label.documents",
		path: "document",
		sortOrder: 90,
	},
	{
		id: "note",
		aggregateType: "obj_note",
		icon: React.createElement(FormOutlined),
		singularKey: "note:label.entity",
		pluralKey: "app:label.notes",
		path: "note",
		sortOrder: 80,
	},
	{
		id: "portfolio",
		aggregateType: "obj_portfolio",
		icon: React.createElement(AppstoreOutlined),
		singularKey: "portfolio:label.entity",
		pluralKey: "app:label.portfolios",
		path: "portfolio",
		sortOrder: 20,
	},
	{
		id: "task",
		aggregateType: "doc_task",
		icon: React.createElement(CheckSquareOutlined),
		singularKey: "task:label.entity",
		pluralKey: "app:label.tasks",
		path: "task",
		sortOrder: 70,
	},
	{
		id: "tenant",
		aggregateType: "obj_tenant",
		icon: React.createElement(FileTextOutlined),
		singularKey: "tenant:label.entity",
		pluralKey: "app:label.tenants",
		path: "tenant",
		sortOrder: 60,
	},
	{
		id: "user",
		aggregateType: "obj_user",
		icon: React.createElement(UserOutlined),
		singularKey: "user:label.entity",
		pluralKey: "app:label.users",
		path: "user",
		sortOrder: 50,
	},
];

// Build lookup maps for efficient access
const itemTypeMap = new Map<string, ItemTypeMeta>(itemTypes.map((t) => [t.id, t]));

// Reverse map: aggregateType (case-insensitive) -> normalized id
const aggregateTypeToId = new Map<string, string>(
	itemTypes.map((t) => [t.aggregateType.toLowerCase(), t.id])
);

/**
 * Normalize itemType ID: "obj_account" -> "account", "account" -> "account"
 * Uses the registry's aggregateType mapping for accurate conversion.
 */
function normalizeItemTypeId(id: string): string {
	const lower = id.toLowerCase();
	// First check if it's an aggregateType (e.g., "obj_account", "doc_task")
	const normalized = aggregateTypeToId.get(lower);
	if (normalized) return normalized;
	// Otherwise assume it's already a normalized id
	return lower;
}

/**
 * Lookup metadata by itemType ID (handles both "obj_account" and "account" formats)
 */
export function getItemTypeMeta(itemTypeId: string | undefined): ItemTypeMeta | undefined {
	if (!itemTypeId) return undefined;
	const normalized = normalizeItemTypeId(itemTypeId);
	return itemTypeMap.get(normalized);
}

/**
 * Get sort order for an itemType (for sorting aggregates in selects)
 * Returns 999 for unknown types to sort them at the end
 */
export function getItemTypeSortOrder(itemTypeId: string | undefined): number {
	return getItemTypeMeta(itemTypeId)?.sortOrder ?? 999;
}

/**
 * Default fallback meta for unknown types
 */
export const defaultItemTypeMeta: ItemTypeMeta = {
	id: "unknown",
	aggregateType: "",
	icon: React.createElement(FileOutlined),
	singularKey: "common:label.unknown",
	pluralKey: "common:label.unknown",
	path: "",
	sortOrder: 999,
};

/**
 * Get all registered item types (useful for debugging or admin views)
 */
export function getAllItemTypes(): ItemTypeMeta[] {
	return [...itemTypes];
}
