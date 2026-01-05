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
	appId?: string;
	id: string;
	name: string;
	icon: string;
	path: string;
	component: string;
}

export type ApplicationAreaMap = { [id: string]: ApplicationArea };
