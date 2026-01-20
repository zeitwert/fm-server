import { create } from "zustand";
import {
	api,
	getRestUrl,
	SESSION_INFO_KEY,
	SESSION_STATE_KEY,
	TENANT_INFO_KEY,
} from "../../common/api/client";
import { changeLanguage } from "../../i18n";
import {
	ADVISOR_TENANT,
	Enumerated,
	KERNEL_TENANT,
	LoginTenantInfo,
	LoginUserInfo,
	ROLE_ADMIN,
	ROLE_APP_ADMIN,
	ROLE_READ_ONLY,
	ROLE_SUPER_USER,
	SessionInfo,
	SessionState,
	TypedEnumerated,
} from "./types";

interface SessionStore {
	// State
	state: SessionState;
	error: string | null;
	userInfo: LoginUserInfo | null;
	selectedTenant: Enumerated | null;
	tenantInfo: LoginTenantInfo | null;
	selectedAccount: Enumerated | null;
	sessionInfo: SessionInfo | null;

	// Computed
	isAuthenticated: () => boolean;
	needsTenantSelection: () => boolean;
	needsAccountSelection: () => boolean;
	isSessionReady: () => boolean;
	availableAccounts: () => Enumerated[];

	// Permission helpers
	/** True if user has APP_ADMIN or ADMIN role */
	isAdmin: () => boolean;
	/** True if user has APP_ADMIN role (kernel tenant admin) */
	isAppAdmin: () => boolean;
	/** True if current tenant is the kernel tenant */
	isKernelTenant: () => boolean;
	/** True if current tenant is an advisor tenant */
	isAdvisorTenant: () => boolean;
	/** True if user has SUPER_USER role */
	hasSuperUserRole: () => boolean;
	/** True if user has READ_ONLY role */
	hasReadOnlyRole: () => boolean;

	// Actions
	login: (email: string, password: string) => Promise<void>;
	selectTenant: (tenant: Enumerated | TypedEnumerated) => Promise<void>;
	selectAccount: (account: Enumerated) => void;
	completeLogin: () => Promise<void>;
	logout: () => Promise<void>;
	initSession: () => Promise<void>;
	clearError: () => void;
	goBackToTenantSelection: () => void;
	switchAccount: (accountId: string) => Promise<void>;
	switchApplication: (appId: string) => void;
}

export const useSessionStore = create<SessionStore>((set, get) => ({
	// Initial state
	state: (sessionStorage.getItem(SESSION_STATE_KEY) as SessionState) || SessionState.close,
	error: null,
	userInfo: null,
	selectedTenant: null,
	tenantInfo: null,
	selectedAccount: null,
	sessionInfo: null,

	// Computed getters
	isAuthenticated: () => {
		const { state } = get();
		return state !== SessionState.close && state !== SessionState.pendingAuth;
	},

	needsTenantSelection: () => {
		const { state, selectedTenant, userInfo } = get();
		return (
			state === SessionState.authenticated &&
			!selectedTenant &&
			(userInfo?.tenants?.length ?? 0) > 0
		);
	},

	needsAccountSelection: () => {
		const { state, selectedTenant, tenantInfo, selectedAccount } = get();
		return (
			state === SessionState.authenticated &&
			selectedTenant !== null &&
			(tenantInfo?.accounts?.length ?? 0) > 1 &&
			selectedAccount === null
		);
	},

	isSessionReady: () => {
		const { state } = get();
		return state === SessionState.open;
	},

	availableAccounts: () => {
		const { tenantInfo } = get();
		return tenantInfo?.accounts ?? [];
	},

	// Permission helpers
	isAdmin: () => {
		const { sessionInfo } = get();
		const roleId = sessionInfo?.user?.role?.id;
		return roleId === ROLE_APP_ADMIN || roleId === ROLE_ADMIN;
	},

	isAppAdmin: () => {
		const { sessionInfo } = get();
		return sessionInfo?.user?.role?.id === ROLE_APP_ADMIN;
	},

	isKernelTenant: () => {
		const { sessionInfo } = get();
		return sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;
	},

	isAdvisorTenant: () => {
		const { sessionInfo } = get();
		return sessionInfo?.tenant?.tenantType?.id === ADVISOR_TENANT;
	},

	hasSuperUserRole: () => {
		const { sessionInfo } = get();
		return sessionInfo?.user?.role?.id === ROLE_SUPER_USER;
	},

	hasReadOnlyRole: () => {
		const { sessionInfo } = get();
		return sessionInfo?.user?.role?.id === ROLE_READ_ONLY;
	},

	// Actions
	login: async (email: string, password: string) => {
		set({ state: SessionState.pendingAuth, error: null });

		try {
			// Authenticate and get user info with tenant list in one call
			const authResponse = await api.login<LoginUserInfo>(getRestUrl("session", "authenticate"), {
				email,
				password,
			});

			if (authResponse.status !== 200) {
				throw new Error("Authentication failed");
			}

			const userInfo = authResponse.data;

			// Update state to authenticated
			sessionStorage.setItem(SESSION_STATE_KEY, SessionState.authenticated);
			set({
				state: SessionState.authenticated,
				userInfo,
				error: null,
			});

			// Auto-select if only one tenant
			if (userInfo.tenants.length === 1) {
				await get().selectTenant(userInfo.tenants[0]);
			}
		} catch (error) {
			sessionStorage.removeItem(SESSION_STATE_KEY);
			set({
				state: SessionState.close,
				error: error instanceof Error ? error.message : "Login failed",
			});
		}
	},

	selectTenant: async (tenant: Enumerated | TypedEnumerated) => {
		// Convert TypedEnumerated to Enumerated for storage
		const enumTenant: Enumerated = { id: tenant.id, name: tenant.name };
		set({ selectedTenant: enumTenant, error: null });

		try {
			// Fetch tenant info with account list
			const tenantInfoResponse = await api.get<LoginTenantInfo>(
				getRestUrl("app", `tenantInfo/${tenant.id}`)
			);

			if (tenantInfoResponse.status !== 200) {
				throw new Error("Failed to fetch tenant info");
			}

			const tenantInfo = tenantInfoResponse.data;
			sessionStorage.setItem(TENANT_INFO_KEY, JSON.stringify(tenantInfo));
			set({ tenantInfo });

			// Auto-complete if no accounts or single account
			if (tenantInfo.accounts.length <= 1) {
				const account = tenantInfo.accounts.length === 1 ? tenantInfo.accounts[0] : null;
				if (account) {
					set({ selectedAccount: account });
				}
				await get().completeLogin();
			}
		} catch (error) {
			sessionStorage.removeItem(SESSION_STATE_KEY);
			sessionStorage.removeItem(TENANT_INFO_KEY);
			set({
				state: SessionState.close,
				error: error instanceof Error ? error.message : "Failed to fetch tenant info",
				selectedTenant: null,
				tenantInfo: null,
				selectedAccount: null,
			});
		}
	},

	selectAccount: (account: Enumerated) => {
		set({ selectedAccount: account });
	},

	completeLogin: async () => {
		const { selectedTenant, selectedAccount } = get();

		set({ state: SessionState.pendingOpen, error: null });

		try {
			// Activate session with tenant and optional account
			const sessionResponse = await api.post<SessionInfo>(getRestUrl("session", "activate"), {
				tenantId: selectedTenant?.id ? parseInt(selectedTenant.id, 10) : null,
				accountId: selectedAccount?.id ? parseInt(selectedAccount.id, 10) : null,
			});

			if (sessionResponse.status !== 200) {
				throw new Error("Failed to activate session");
			}

			const sessionInfo = sessionResponse.data;

			// Normalize IDs to strings
			sessionInfo.tenant.id = sessionInfo.tenant.id.toString();
			sessionInfo.user.id = sessionInfo.user.id.toString();
			if (sessionInfo.account?.id) {
				sessionInfo.account.id = sessionInfo.account.id.toString();
			}

			// Persist to session storage
			sessionStorage.setItem(SESSION_STATE_KEY, SessionState.open);
			sessionStorage.setItem(SESSION_INFO_KEY, JSON.stringify(sessionInfo));

			// Sync i18n language with session locale
			changeLanguage(sessionInfo.locale);

			set({
				state: SessionState.open,
				sessionInfo,
				error: null,
			});
		} catch (error) {
			set({
				state: SessionState.authenticated,
				error: error instanceof Error ? error.message : "Failed to complete login",
			});
		}
	},

	logout: async () => {
		try {
			await api.post(getRestUrl("session", "logout"), {});
		} catch {
			// Ignore logout errors
		} finally {
			sessionStorage.removeItem(SESSION_STATE_KEY);
			sessionStorage.removeItem(SESSION_INFO_KEY);
			sessionStorage.removeItem(TENANT_INFO_KEY);
			set({
				state: SessionState.close,
				error: null,
				userInfo: null,
				selectedTenant: null,
				tenantInfo: null,
				selectedAccount: null,
				sessionInfo: null,
			});
			window.location.replace("/login");
		}
	},

	initSession: async () => {
		const storedState = sessionStorage.getItem(SESSION_STATE_KEY) as SessionState | null;
		const storedSessionInfo = sessionStorage.getItem(SESSION_INFO_KEY);
		const storedTenantInfo = sessionStorage.getItem(TENANT_INFO_KEY);

		if (storedState === SessionState.open && storedSessionInfo) {
			try {
				const sessionInfo = JSON.parse(storedSessionInfo) as SessionInfo;
				const tenantInfo = storedTenantInfo
					? (JSON.parse(storedTenantInfo) as LoginTenantInfo)
					: null;

				// Sync i18n language with session locale
				changeLanguage(sessionInfo.locale);

				set({
					state: SessionState.open,
					sessionInfo,
					tenantInfo,
				});
			} catch {
				// Invalid stored session, clear it
				sessionStorage.removeItem(SESSION_STATE_KEY);
				sessionStorage.removeItem(SESSION_INFO_KEY);
				sessionStorage.removeItem(TENANT_INFO_KEY);
				set({ state: SessionState.close });
			}
		} else if (storedState === SessionState.authenticated) {
			// User was in the middle of tenant/account selection, restart login
			sessionStorage.removeItem(SESSION_STATE_KEY);
			sessionStorage.removeItem(TENANT_INFO_KEY);
			set({ state: SessionState.close });
		}
	},

	clearError: () => {
		set({ error: null });
	},

	goBackToTenantSelection: () => {
		set({
			selectedTenant: null,
			tenantInfo: null,
			selectedAccount: null,
			error: null,
		});
	},

	switchAccount: async (accountId: string) => {
		const { sessionInfo } = get();
		if (!sessionInfo) return;

		set({ error: null });

		try {
			// Call backend to switch account context
			const sessionResponse = await api.post<SessionInfo>(getRestUrl("session", "activate"), {
				tenantId: parseInt(sessionInfo.tenant.id, 10),
				accountId: parseInt(accountId, 10),
			});

			if (sessionResponse.status !== 200) {
				throw new Error("Failed to switch account");
			}

			const newSessionInfo = sessionResponse.data;

			// Normalize IDs to strings
			newSessionInfo.tenant.id = newSessionInfo.tenant.id.toString();
			newSessionInfo.user.id = newSessionInfo.user.id.toString();
			if (newSessionInfo.account?.id) {
				newSessionInfo.account.id = newSessionInfo.account.id.toString();
			}

			// Update session storage
			sessionStorage.setItem(SESSION_INFO_KEY, JSON.stringify(newSessionInfo));

			// Sync i18n language if locale changed
			changeLanguage(newSessionInfo.locale);

			set({
				sessionInfo: newSessionInfo,
				selectedAccount: newSessionInfo.account
					? { id: newSessionInfo.account.id, name: newSessionInfo.account.name }
					: null,
			});
		} catch (error) {
			set({
				error: error instanceof Error ? error.message : "Failed to switch account",
			});
		}
	},

	switchApplication: (appId: string) => {
		const { sessionInfo } = get();
		if (!sessionInfo) return;

		// Validate appId is in available applications
		if (!sessionInfo.availableApplications.includes(appId)) {
			return;
		}

		// Update sessionInfo with new applicationId
		const updatedSessionInfo: SessionInfo = {
			...sessionInfo,
			applicationId: appId,
		};

		// Persist to session storage
		sessionStorage.setItem(SESSION_INFO_KEY, JSON.stringify(updatedSessionInfo));

		set({ sessionInfo: updatedSessionInfo });
	},
}));
