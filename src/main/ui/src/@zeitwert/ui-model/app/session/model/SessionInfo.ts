
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { Locale } from "../../common";
import { Application } from "./Application";

export const KERNEL_TENANT = "kernel";
export const ADVISOR_TENANT = "advisor";
export const COMMUNITY_TENANT = "community";

export interface TenantInfo {
	id: string;
	caption: string;
	name: string;
	extlKey: string;
	tenantType: Enumerated;
}

export interface UserInfo {
	id: string;
	caption: string;
	name: string;
	tenant: Enumerated;
	email: string;
	role: Enumerated;
}

export interface SessionInfo {
	tenant: TenantInfo;
	user: UserInfo;
	account: Enumerated;
	locale: Locale;
	applicationId: string;
	applications: Application[];
}
