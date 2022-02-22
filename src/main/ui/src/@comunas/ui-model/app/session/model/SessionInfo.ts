import { CodeItemType, Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { Locale } from "../../common";
import { Application } from "./Application";

export enum EmailProvider {
	GOOGLE = "google",
	MICROSOFT = "exchange"
}

export interface TenantInfo {
	id: string;
	caption: string;
	name: string;
	extlKey: string;
}

export interface CommunityInfo {
	id: string;
	name: string;
	itemType: CodeItemType | undefined;
}

export interface UserInfo {
	id: string;
	caption: string;
	name: string;
	emailProvider: Enumerated;
	email: string;
	extlIdpUserId?: string;
	picture?: string;
	roles: string[];
	communities: CommunityInfo[];
}

export interface LoginInfo {
	id: number;
	email: string;
	username: string;
	type: string;
	token: string;
	roles: string[];
	customValues: any;
}

export interface SessionInfo {
	tenant: TenantInfo;
	user: UserInfo;
	locale: Locale;
	applicationId: string;
	applications: Application[];
	customValues: any;
}
