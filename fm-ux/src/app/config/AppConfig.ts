// Application configuration - defines all apps and their areas
import type { ReactNode } from 'react';
import {
	AppstoreOutlined,
	BankOutlined,
	CheckSquareOutlined,
	DashboardOutlined,
	FileTextOutlined,
	HomeOutlined,
	TeamOutlined,
	UserOutlined,
} from '@ant-design/icons';
import React from 'react';

export interface Application {
	id: string;
	name: string;
	description: string;
}

export interface ApplicationArea {
	id: string;
	name: string;
	icon: ReactNode;
	path: string;
}

export interface ApplicationInfo {
	id: string;
	name: string;
	areas: ApplicationArea[];
	defaultArea: string;
}

// Application Areas
const homeArea: ApplicationArea = {
	id: 'home',
	name: 'Dashboard',
	icon: React.createElement(DashboardOutlined),
	path: 'home',
};

const tenantArea: ApplicationArea = {
	id: 'tenant',
	name: 'Mandanten',
	icon: React.createElement(FileTextOutlined),
	path: 'tenant',
};

const userArea: ApplicationArea = {
	id: 'user',
	name: 'Benutzer',
	icon: React.createElement(UserOutlined),
	path: 'user',
};

const accountArea: ApplicationArea = {
	id: 'account',
	name: 'Kunden',
	icon: React.createElement(BankOutlined),
	path: 'account',
};

const contactArea: ApplicationArea = {
	id: 'contact',
	name: 'Kontakte',
	icon: React.createElement(TeamOutlined),
	path: 'contact',
};

const portfolioArea: ApplicationArea = {
	id: 'portfolio',
	name: 'Portfolios',
	icon: React.createElement(AppstoreOutlined),
	path: 'portfolio',
};

const buildingArea: ApplicationArea = {
	id: 'building',
	name: 'Immobilien',
	icon: React.createElement(HomeOutlined),
	path: 'building',
};

const documentArea: ApplicationArea = {
	id: 'document',
	name: 'Dokumente',
	icon: React.createElement(FileTextOutlined),
	path: 'document',
};

const taskArea: ApplicationArea = {
	id: 'task',
	name: 'Aufgaben',
	icon: React.createElement(CheckSquareOutlined),
	path: 'task',
};

// Applications
const fmApp: Application = {
	id: 'fm',
	name: 'zeitwert: fm',
	description: 'Strategische Unterhaltsplanung',
};

const tenantAdminApp: Application = {
	id: 'tenantAdmin',
	name: 'ZEitWERT: admin',
	description: 'Mandantenadministration',
};

const appAdminApp: Application = {
	id: 'appAdmin',
	name: 'ZEitWERT: appAdmin',
	description: 'Applikationsadministration',
};

// Application Infos (with areas)
const fmAppInfo: ApplicationInfo = {
	id: 'fmMenu',
	name: 'ZEitWERT: fm',
	areas: [homeArea, portfolioArea, buildingArea, taskArea, accountArea, contactArea],
	defaultArea: 'home',
};

const tenantAdminAppInfo: ApplicationInfo = {
	id: 'adminMenu',
	name: 'ZEitWERT: admin',
	areas: [tenantArea, accountArea, userArea],
	defaultArea: 'user',
};

const appAdminAppInfo: ApplicationInfo = {
	id: 'appAdminMenu',
	name: 'ZEitWERT: appAdmin',
	areas: [tenantArea, accountArea, userArea],
	defaultArea: 'tenant',
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

