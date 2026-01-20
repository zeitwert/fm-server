// Session state enum
export enum SessionState {
	close = "close",
	pendingAuth = "pendingAuth",
	authenticated = "authenticated",
	pendingOpen = "pendingOpen",
	open = "open",
}

// Basic enumerated type for dropdowns
export interface Enumerated {
	id: string;
	name: string;
}

// Typed enumerated (includes itemType for aggregates)
export interface TypedEnumerated {
	id: string;
	name: string;
	itemType: Enumerated;
}

// Login flow types
export interface LoginUserInfo {
	id: string;
	name: string;
	email: string;
	role: Enumerated;
	tenants: TypedEnumerated[];
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
	sessionId: string;
	role: string;
}

// Session info types
export interface DocumentInfo {
	id: string;
	caption: string;
	name: string;
	contentKind: Enumerated;
	contentType: Enumerated | undefined;
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

export interface SessionInfo {
	tenant: TenantInfo;
	user: UserInfo;
	account?: AccountInfo;
	locale: string;
	applicationId: string;
	applicationName: string;
	applicationVersion: string;
	availableApplications: string[];
}

// Tenant type constants
export const KERNEL_TENANT = "kernel";
export const ADVISOR_TENANT = "advisor";
export const COMMUNITY_TENANT = "community";

// User role constants (matching CodeUserRole enum on server, lowercase IDs)
export const ROLE_APP_ADMIN = "app_admin";
export const ROLE_ADMIN = "admin";
export const ROLE_SUPER_USER = "super_user";
export const ROLE_USER = "user";
export const ROLE_READ_ONLY = "read_only";
