export const SELF_TARGET = "self";

export const ROUTE_ACTION = "route";

export interface Application {
	id: string;
	name: string;
	icon: string;
	description: string;
}

export interface ApplicationInfo {
	id: string;
	name: string;
	areas: ApplicationArea[];
	defaultArea: string;
}

export interface ApplicationArea {
	appId: string;
	id: string;
	name: string;
	icon: string;
	path: string;
	component: string;
	menu?: Menu;
	menuAction?: MenuAction;
}

export type ApplicationAreaMap = { [id: string]: ApplicationArea };

export interface Menu {
	items: MenuItem[];
}

export type MenuItem = MenuHeader | MenuAction;

export interface MenuHeader {
	_type: "zeitwert.app.domain.MenuHeader";
	id: string;
	name: string;
}

export interface MenuAction {
	_type: "zeitwert.app.domain.MenuAction";
	id: string;
	name: string;
	navigation: Navigation;
	icon: string;
}

export interface Navigation {
	target: NavigationTarget;
	action: NavigationAction;
}

export interface NavigationTarget {
	applicationId: string;
	applicationAreaId: string;
}

export interface NavigationAction {
	actionType: string;
	params: any;
}
