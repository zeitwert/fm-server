import { Enumerated } from "@zeitwert/ui-model/ddd";

export interface LoginUserInfo {
	id: string;
	name: string;
	email: string;
	role: Enumerated;
	tenants: Enumerated[];
}

export interface LoginTenantInfo {
	id: string;
	tenantType: Enumerated;
	accounts: Enumerated[];
}

export interface LoginInfo {
	id: number;
	email: string;
	username: string;
	accountId: number;
	tokenType: string;
	token: string;
	role: string;
}
