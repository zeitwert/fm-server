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

export interface AccountInfo {
	id: string;
	name: string;
	itemType: CodeItemType | undefined;
}

export interface UserInfo {
	id: string;
	caption: string;
	name: string;
	tenant: Enumerated;
	emailProvider: Enumerated;
	email: string;
	extlIdpUserId?: string;
	picture?: string;
	roles: string[];
	accounts: AccountInfo[];
}

export interface LoginInfo {
	id: number;
	email: string;
	username: string;
	accountId: number;
	tokenType: string;
	token: string;
	roles: string[];
}

export interface SessionInfo {
	tenant: TenantInfo;
	user: UserInfo;
	account: AccountInfo;
	locale: Locale;
	applicationId: string;
	applications: Application[];
}
