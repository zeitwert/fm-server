// Application configuration - defines all apps and their areas
import type { ReactNode } from "react";
import type { TFunction } from "i18next";
import { DashboardOutlined } from "@ant-design/icons";
import React from "react";
import { getItemTypeMeta } from "./ItemTypeRegistry";

export interface Application {
	id: string;
	name: string;
	appKey: string; // Two-letter abbreviation for collapsed sidebar display
	shortName: string; // Translation key for short name display (e.g., in segmented control)
	description: string;
}

export interface ApplicationArea {
	id: string;
	labelKey: string; // Translation key in 'app' namespace
	label: (t: TFunction) => string; // Returns translated label
	icon: ReactNode;
	path: string;
}

export interface ApplicationInfo {
	id: string;
	name: string;
	areas: ApplicationArea[];
	defaultArea: string;
}

// Helper to create area with translation support
function createArea(id: string, labelKey: string, icon: ReactNode, path: string): ApplicationArea {
	return {
		id,
		labelKey,
		label: (t: TFunction) => t(labelKey),
		icon,
		path,
	};
}

// Helper to create area from ItemTypeRegistry
function createAreaFromItemType(id: string): ApplicationArea {
	const meta = getItemTypeMeta(id);
	if (!meta) {
		throw new Error(`Unknown itemType: ${id}`);
	}
	return {
		id: meta.id,
		labelKey: meta.pluralKey,
		label: (t: TFunction) => t(meta.pluralKey),
		icon: meta.icon,
		path: meta.path,
	};
}

// Application Areas
const homeArea = createArea(
	"home",
	"app:label.dashboard",
	React.createElement(DashboardOutlined),
	"home"
);
const tenantArea = createAreaFromItemType("tenant");
const userArea = createAreaFromItemType("user");
const accountArea = createAreaFromItemType("account");
const contactArea = createAreaFromItemType("contact");
const portfolioArea = createAreaFromItemType("portfolio");
const buildingArea = createAreaFromItemType("building");
const documentArea = createAreaFromItemType("document");
const taskArea = createAreaFromItemType("task");
const noteArea = createAreaFromItemType("note");

// Applications
const fmApp: Application = {
	id: "fm",
	name: "ZEitWERT: fm",
	appKey: "FM",
	shortName: "app:label.appFm",
	description: "Strategische Unterhaltsplanung",
};

const fmAdminApp: Application = {
	id: "fmAdmin",
	name: "ZEitWERT: fmAdmin",
	appKey: "AD",
	shortName: "app:label.appFmAdmin",
	description: "Kunden Administration",
};

const tenantAdminApp: Application = {
	id: "tenantAdmin",
	name: "ZEitWERT: admin",
	appKey: "MA",
	shortName: "app:label.appTenantAdmin",
	description: "Mandantenadministration",
};

const appAdminApp: Application = {
	id: "appAdmin",
	name: "ZEitWERT: appAdmin",
	appKey: "AP",
	shortName: "app:label.appAppAdmin",
	description: "Applikationsadministration",
};

// Application Infos (with areas)
const fmAppInfo: ApplicationInfo = {
	id: "fmMenu",
	name: "ZEitWERT: fm",
	areas: [homeArea, portfolioArea, buildingArea, contactArea, taskArea, noteArea],
	defaultArea: "home",
};

const fmAdminAppInfo: ApplicationInfo = {
	id: "fmAdminMenu",
	name: "ZEitWERT: fmAdmin",
	areas: [tenantArea, accountArea, userArea],
	defaultArea: "home",
};

const tenantAdminAppInfo: ApplicationInfo = {
	id: "adminMenu",
	name: "ZEitWERT: admin",
	areas: [tenantArea, accountArea, userArea],
	defaultArea: "user",
};

const appAdminAppInfo: ApplicationInfo = {
	id: "appAdminMenu",
	name: "ZEitWERT: appAdmin",
	areas: [tenantArea, accountArea, userArea],
	defaultArea: "tenant",
};

// Export configuration maps
export const ApplicationMap: { [id: string]: Application } = {
	[fmApp.id]: fmApp,
	[fmAdminApp.id]: fmAdminApp,
	[tenantAdminApp.id]: tenantAdminApp,
	[appAdminApp.id]: appAdminApp,
};

export const ApplicationInfoMap: { [id: string]: ApplicationInfo } = {
	[fmApp.id]: fmAppInfo,
	[fmAdminApp.id]: fmAdminAppInfo,
	[tenantAdminApp.id]: tenantAdminAppInfo,
	[appAdminApp.id]: appAdminAppInfo,
};

export const AreaMap: { [id: string]: ApplicationArea } = {
	[homeArea.id]: homeArea,
	[tenantArea.id]: tenantArea,
	[userArea.id]: userArea,
	[accountArea.id]: accountArea,
	[contactArea.id]: contactArea,
	[portfolioArea.id]: portfolioArea,
	[buildingArea.id]: buildingArea,
	[documentArea.id]: documentArea,
	[taskArea.id]: taskArea,
	[noteArea.id]: noteArea,
};

// Helper functions
export function getApplicationList(allowedAppIds: string[]): Application[] {
	return allowedAppIds
		.map((id) => ApplicationMap[id])
		.filter((app): app is Application => app !== undefined);
}

export function getApplicationInfo(appId: string): ApplicationInfo | undefined {
	return ApplicationInfoMap[appId];
}

export function getArea(areaId: string): ApplicationArea | undefined {
	return AreaMap[areaId];
}

export function useAreaIcon(areaId: string): ReactNode | undefined {
	return AreaMap[areaId]?.icon;
}

export const AppConfig = {
	ApplicationMap,
	ApplicationInfoMap,
	AreaMap,
	getApplicationList,
	getApplicationInfo,
	getArea,
};

export default AppConfig;
