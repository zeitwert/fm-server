import type { LoginUserInfo, LoginTenantInfo, SessionInfo } from "@/session/model/types";

// Test user with single tenant and single account (auto-complete flow)
export const singleTenantUser: LoginUserInfo = {
	id: "1",
	name: "Test User",
	email: "test@example.com",
	role: { id: "admin", name: "Administrator" },
	tenants: [{ id: "100", name: "Test Tenant", itemType: { id: "advisor", name: "Advisor" } }],
};

// Test user with multiple tenants
export const multiTenantUser: LoginUserInfo = {
	id: "2",
	name: "Multi Tenant User",
	email: "multi@example.com",
	role: { id: "admin", name: "Administrator" },
	tenants: [
		{ id: "100", name: "Tenant Alpha", itemType: { id: "advisor", name: "Advisor" } },
		{ id: "200", name: "Tenant Beta", itemType: { id: "community", name: "Community" } },
	],
};

// Tenant info with single account (auto-complete)
export const singleAccountTenantInfo: LoginTenantInfo = {
	id: "100",
	tenantType: { id: "advisor", name: "Advisor" },
	accounts: [{ id: "1000", name: "Default Account" }],
};

// Tenant info with multiple accounts
export const multiAccountTenantInfo: LoginTenantInfo = {
	id: "100",
	tenantType: { id: "advisor", name: "Advisor" },
	accounts: [
		{ id: "1000", name: "Account One" },
		{ id: "2000", name: "Account Two" },
	],
};

// Session info after successful activation
export const activeSessionInfo: SessionInfo = {
	tenant: {
		id: "100",
		caption: "Test Tenant",
		name: "Test Tenant",
		extlKey: "test-tenant",
		tenantType: { id: "advisor", name: "Advisor" },
		logo: undefined,
	},
	user: {
		id: "1",
		caption: "Test User",
		name: "Test User",
		tenant: { id: "100", name: "Test Tenant" },
		email: "test@example.com",
		role: { id: "admin", name: "Administrator" },
	},
	account: {
		id: "1000",
		caption: "Default Account",
		name: "Default Account",
		accountType: { id: "standard", name: "Standard" },
		logo: undefined,
	},
	locale: "de",
	applicationId: "fm",
	applicationName: "ZEitWERT: fm",
	applicationVersion: "0.0.1-SNAPSHOT",
	availableApplications: ["fm"],
};

// Session info for super user with multiple applications
export const superUserSessionInfo: SessionInfo = {
	tenant: {
		id: "100",
		caption: "Test Tenant",
		name: "Test Tenant",
		extlKey: "test-tenant",
		tenantType: { id: "advisor", name: "Advisor" },
		logo: undefined,
	},
	user: {
		id: "2",
		caption: "Super User",
		name: "Super User",
		tenant: { id: "100", name: "Test Tenant" },
		email: "super@example.com",
		role: { id: "superUser", name: "Super User" },
	},
	account: {
		id: "1000",
		caption: "Default Account",
		name: "Default Account",
		accountType: { id: "standard", name: "Standard" },
		logo: undefined,
	},
	locale: "de",
	applicationId: "fm",
	applicationName: "ZEitWERT: fm",
	applicationVersion: "0.0.1-SNAPSHOT",
	availableApplications: ["fm", "fmAdmin"],
};

// Home dashboard data
export const homeOverview = {
	accountId: 1000,
	accountName: "Default Account",
	buildingCount: 25,
	portfolioCount: 5,
	insuranceValue: 15000000,
	timeValue: 12000000,
	shortTermRenovationCosts: 500000,
	midTermRenovationCosts: 1200000,
	ratingCount: 18,
};

export const homeOpenActivities = [
	{
		item: { id: "1", name: "Task 1", itemType: { id: "task", name: "Task" } },
		relatedTo: { id: "101", name: "Building A", itemType: { id: "building", name: "Building" } },
		owner: { id: "1", name: "Test User" },
		dueAt: new Date().toISOString(),
		subject: "Review inspection report",
		content: "Please review the inspection report for Building A",
		priority: { id: "high", name: "High" },
	},
];

export const homeRecentActions = [
	{
		item: { id: "101", name: "Building A", itemType: { id: "building", name: "Building" } },
		seqNr: 1,
		timestamp: new Date().toISOString(),
		user: { id: "1", name: "Test User" },
	},
];

export const homeBuildings = {
	data: [
		{
			id: "101",
			attributes: {
				name: "Building A",
				street: "Main Street 1",
				zip: "12345",
				city: "Test City",
				geoCoordinates: "WGS:48.1351,11.5820",
			},
		},
		{
			id: "102",
			attributes: {
				name: "Building B",
				street: "Oak Avenue 5",
				zip: "12345",
				city: "Test City",
				geoCoordinates: "WGS:48.1400,11.5900",
			},
		},
	],
};
