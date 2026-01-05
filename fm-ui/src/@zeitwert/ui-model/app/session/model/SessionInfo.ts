
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { Locale } from "../../common";

export const KERNEL_TENANT = "kernel";
export const ADVISOR_TENANT = "advisor";
export const COMMUNITY_TENANT = "community";

export interface SessionInfo {
	tenant: TenantInfo;
	user: UserInfo;
	account?: AccountInfo;
	locale: Locale;
	applicationId: string;
	applicationName: string;
	applicationVersion: string;
	availableApplications: string[];
}

export interface TenantInfo {
	id: string;
	caption: string;
	name: string;
	extlKey: string;
	tenantType: Enumerated;
	logo: DocumentInfo | undefined;
}

export interface UserInfo {
	id: string;
	caption: string;
	name: string;
	tenant: Enumerated;
	email: string;
	role: Enumerated;
}

export interface AccountInfo {
	id: string;
	caption: string;
	name: string;
	accountType: Enumerated;
	logo: DocumentInfo | undefined;
}

export interface DocumentInfo {
	id: string;
	caption: string;
	name: string;
	contentKind: Enumerated;
	contentType: Enumerated | undefined;
}
