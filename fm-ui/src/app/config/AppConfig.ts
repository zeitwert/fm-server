// Application configuration - defines all apps and their areas

export interface Application {
	id: string;
	name: string;
	icon: string;
	description: string;
}

export interface ApplicationArea {
	id: string;
	name: string;
	icon: string;
	path: string;
	component: string;
}

export interface ApplicationInfo {
	id: string;
	name: string;
	areas: ApplicationArea[];
	defaultArea: string;
}

// Application Areas
const homeArea: ApplicationArea = {
	id: "home",
	name: "Dashboard",
	icon: "standard:home",
	path: "home",
	component: "home/ui/HomeArea",
};

const tenantArea: ApplicationArea = {
	id: "tenant",
	name: "Mandanten",
	icon: "standard:document",
	path: "tenant",
	component: "tenant/ui/TenantArea",
};

const userArea: ApplicationArea = {
	id: "user",
	name: "Benutzer",
	icon: "standard:user",
	path: "user",
	component: "user/ui/UserArea",
};

const accountArea: ApplicationArea = {
	id: "account",
	name: "Kunden",
	icon: "standard:account",
	path: "account",
	component: "account/ui/AccountArea",
};

const contactArea: ApplicationArea = {
	id: "contact",
	name: "Kontakte",
	icon: "standard:contact",
	path: "contact",
	component: "contact/ui/ContactArea",
};

const portfolioArea: ApplicationArea = {
	id: "portfolio",
	name: "Portfolios",
	icon: "standard:store_group",
	path: "portfolio",
	component: "portfolio/ui/PortfolioArea",
};

const buildingArea: ApplicationArea = {
	id: "building",
	name: "Immobilien",
	icon: "custom:custom24",
	path: "building",
	component: "building/ui/BuildingArea",
};

const documentArea: ApplicationArea = {
	id: "document",
	name: "Dokumente",
	icon: "standard:document",
	path: "document",
	component: "document/ui/DocumentArea",
};

const taskArea: ApplicationArea = {
	id: "task",
	name: "Aufgaben",
	icon: "standard:task",
	path: "task",
	component: "task/ui/TaskArea",
};

// Applications
const fmApp: Application = {
	id: "fm",
	name: "zeitwert: fm",
	icon: "advise",
	description: "Strategische Unterhaltsplanung",
};

const tenantAdminApp: Application = {
	id: "tenantAdmin",
	name: "ZEitWERT: admin",
	icon: "config",
	description: "Mandantenadministration",
};

const appAdminApp: Application = {
	id: "appAdmin",
	name: "ZEitWERT: appAdmin",
	icon: "config",
	description: "Applikationsadministration",
};

// Application Infos (with areas)
const fmAppInfo: ApplicationInfo = {
	id: "fmMenu",
	name: "ZEitWERT: fm",
	areas: [homeArea, portfolioArea, buildingArea, taskArea, accountArea, contactArea],
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
	[tenantAdminApp.id]: tenantAdminApp,
	[appAdminApp.id]: appAdminApp,
};

export const ApplicationInfoMap: { [id: string]: ApplicationInfo } = {
	[fmApp.id]: fmAppInfo,
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

export const AppConfig = {
	ApplicationMap,
	ApplicationInfoMap,
	AreaMap,
	getApplicationList,
	getApplicationInfo,
	getArea,
};

export default AppConfig;
