// Application configuration - defines all apps and their areas
import type { ReactNode } from 'react';
import type { TFunction } from 'i18next';
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
function createArea(
	id: string,
	labelKey: string,
	icon: ReactNode,
	path: string
): ApplicationArea {
	return {
		id,
		labelKey,
		label: (t: TFunction) => t(labelKey),
		icon,
		path,
	};
}

// Application Areas
const homeArea = createArea('home', 'dashboard', React.createElement(DashboardOutlined), 'home');
const tenantArea = createArea('tenant', 'tenants', React.createElement(FileTextOutlined), 'tenant');
const userArea = createArea('user', 'users', React.createElement(UserOutlined), 'user');
const accountArea = createArea('account', 'accounts', React.createElement(BankOutlined), 'account');
const contactArea = createArea('contact', 'contacts', React.createElement(TeamOutlined), 'contact');
const portfolioArea = createArea(
	'portfolio',
	'portfolios',
	React.createElement(AppstoreOutlined),
	'portfolio'
);
const buildingArea = createArea(
	'building',
	'buildings',
	React.createElement(HomeOutlined),
	'building'
);
const documentArea = createArea(
	'document',
	'documents',
	React.createElement(FileTextOutlined),
	'document'
);
const taskArea = createArea(
	'task',
	'tasks',
	React.createElement(CheckSquareOutlined),
	'task'
);

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
