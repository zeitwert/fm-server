
import { Enumerated, session, Session, TenantInfo, UserInfo } from "@zeitwert/ui-model";
import { flow, types } from "mobx-state-tree";

export const isValidEmail = (email: string | undefined): boolean => {
	return !!email && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(email);
};

export const LoginModel = types
	.model("Login", {
		email: types.maybe(types.string),
		tenant: types.maybe(types.frozen<TenantInfo>()),
		password: types.maybe(types.string),
		accounts: types.optional(types.array(types.frozen<Enumerated>()), []),
		account: types.maybe(types.frozen<Enumerated>()),
	})
	.views((self) => ({
		get isValidEmail(): boolean {
			return isValidEmail(self.email);
		},
	}))
	.views((self) => ({
		get isReadyForLogin(): boolean {
			return isValidEmail(self.email) && !!self.password && !!self.account;
		}
	}))
	.views((self) => ({
		get hasTenant(): boolean {
			return !!self.tenant;
		},
		get tenantLogoUrl(): string | undefined {
			return !!self.tenant ? `/tenant/${self.tenant.extlKey}/login-logo.jpg` : "/tenant/login-logo.jpg";
		},
	}))
	.actions((self) => ({
		loadAccounts(email: string) {
			return flow<UserInfo, any[]>(function* (): any {
				try {
					self.tenant = undefined;
					self.account = undefined;
					const userInfoResponse = yield session.userInfo(email);
					if (userInfoResponse?.tenant) {
						self.tenant = userInfoResponse.tenant;
						self.accounts = userInfoResponse.accounts;
						if (userInfoResponse?.accounts.length === 1) {
							self.account = userInfoResponse.accounts[0];
						}
					}
					return userInfoResponse;
				} catch (error: any) {
					console.error("Failed to load userInfo", error);
					return Promise.reject(error);
				}
			})();
		},
		async login(session: Session) {
			if (self.isReadyForLogin) {
				await session.login(self.email!, self.password!, self.account);
				if (!session.isAuthenticated) {
					alert("Could not log in!");
				} else {
					window.location.href = "/";
				}
			}
		}
	}));

export const LoginData = LoginModel.create({});
