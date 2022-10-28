
import { AxiosResponse } from "axios";
import Logger from "loglevel";
import { observable, transaction } from "mobx";
import { applySnapshot, flow, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import {
	API,
	AUTH_HEADER_ITEM,
	Config,
	Formatter,
	Languages,
	Locale,
	Locales,
	SESSION_INFO_ITEM,
	SESSION_STATE_ITEM,
	Translator
} from "../../common";
import { FormatterImpl } from "../../common/i18n/impl/FormatterImpl";
import { TranslatorImpl } from "../../common/i18n/impl/TranslatorImpl";
import { Application, ApplicationArea, ApplicationAreaMap, ApplicationInfo } from "./Application";
import { LoginInfo } from "./LoginInfo";
import { ADVISOR_TENANT, COMMUNITY_TENANT, KERNEL_TENANT, SessionInfo, UserInfo } from "./SessionInfo";

export enum SessionState {
	close = "close",
	pendingAuth = "pendingAuth",
	pendingOpen = "pendingOpen",
	open = "open",
	pendingClose = "pendingClose"
}

const USER_INFO_URL = "userInfo";
const LOGIN_URL = "login";
const LOGOUT_URL = "logout";
const SESSION_URL = "session";
const APP_LIST_URL = "applications";

const MstSessionModel = types
	.model("Session", {
		state: types.optional(types.string, sessionStorage.getItem(SESSION_STATE_ITEM) || SessionState.close),
		locale: types.maybe(types.enumeration((Languages as string[]).concat(Locales as string[]))),
		sessionInfo: types.maybe(types.frozen<SessionInfo>()),
		appList: types.maybe(types.frozen<Application[]>()),
		appInfo: types.maybe(types.frozen<ApplicationInfo>()),
		appAreaMap: types.maybe(types.frozen<ApplicationAreaMap>()),
		helpContext: types.optional(types.string, ""),
		networkActivityCount: types.optional(types.number, 0)
	})
	.volatile(() => ({
		initialState: {} as any
	}))
	.actions((self) => {
		return {
			afterCreate() {
				self.initialState = getSnapshot(self);
			},
			reset() {
				applySnapshot(self, self.initialState);
			}
		};
	})
	.actions((self) => ({
		setState(state: SessionState) {
			sessionStorage.setItem(SESSION_STATE_ITEM, state);
			self.state = state;
		}
	}))
	.views((self) => ({
		get isNetworkActive(): boolean {
			return self.networkActivityCount > 0;
		},
	}))
	.actions((self) => ({
		startNetwork() {
			self.networkActivityCount += 1;
		},
		stopNetwork() {
			self.networkActivityCount -= 1;
		}
	}))
	.actions((self) => ({
		setHelpContext(ctx: string) {
			self.helpContext = ctx;
		}
	}))
	.actions((self) => ({
		setLocale(locale: Locale) {
			self.locale = locale;
		},
		clear(state: SessionState) {
			transaction(() => {
				self.setState(state);
				self.sessionInfo = undefined;
				self.appList = undefined;
				self.appInfo = undefined;
				self.appAreaMap = undefined;
			});
		},
		setApp(appId: string) {
			return flow(function* () {
				try {
					const appResponse = yield API.get(Config.getApiUrl("app", APP_LIST_URL + "/" + appId));
					self.appInfo = appResponse.data;
					self.appAreaMap = self.appInfo!.areas.reduce(
						(map: ApplicationAreaMap, area: ApplicationArea): ApplicationAreaMap => {
							area.appId = appId;
							map[area.id] = area;
							return map;
						},
						{} as ApplicationAreaMap
					);
					self.sessionInfo = Object.assign({}, self.sessionInfo, { applicationId: appId });
					sessionStorage.setItem(SESSION_INFO_ITEM, JSON.stringify(self.sessionInfo));
				} catch (error: any) {
					Logger.error("Failed to load application", error);
				}
			})();
		}
	}))
	.actions((self) => ({
		init(oldSessionInfo?: SessionInfo) {
			return flow(function* () {
				try {
					self.clear(SessionState.pendingOpen);
					let sessionInfo: SessionInfo;
					if (!oldSessionInfo) {
						const sessionResponse: AxiosResponse<SessionInfo> = yield API.get(Config.getApiUrl("session", SESSION_URL));
						sessionInfo = sessionResponse.data;
						sessionStorage.setItem(SESSION_INFO_ITEM, JSON.stringify(sessionInfo));
					} else {
						sessionInfo = oldSessionInfo;
					}
					const appListResponse = yield API.get(Config.getApiUrl("app", APP_LIST_URL));
					const appList = appListResponse.data;
					transaction(() => {
						self.sessionInfo = sessionInfo;
						self.appList = appList;
						self.setApp(sessionInfo.applicationId);
						self.setLocale(sessionInfo.locale);
						self.setState(SessionState.open);
					});
				} catch (error: any) {
					self.setState(SessionState.close);
					Logger.error("Failed to initialize user session", error);
				}
			})();
		}
	}))
	.extend((self) => {
		const isAuthenticated = observable.box(!!sessionStorage.getItem(SESSION_INFO_ITEM) && !!sessionStorage.getItem(AUTH_HEADER_ITEM));
		const isAuthAfterGlow = observable.box(false);
		return {
			views: {
				get isAuthenticated() {
					return isAuthenticated.get();
				},
				get isInit() {
					return self.state === SessionState.open && !!self.sessionInfo;
				},
				get doShowLoginForm(): boolean {
					return !isAuthenticated.get() || isAuthAfterGlow.get();
				},
			},
			actions: {
				initSession() {
					const sessionInfo = sessionStorage.getItem(SESSION_INFO_ITEM);
					if (!!sessionInfo) {
						return self.init(JSON.parse(sessionInfo));
					}
					return Promise.resolve();
				},
				userInfo(email: string): Promise<UserInfo | undefined> {
					return flow(function* () {
						try {
							const userInfoResponse: AxiosResponse<UserInfo> = yield API.get(Config.getApiUrl("app", USER_INFO_URL + "/" + email));
							if (userInfoResponse.status === 200) {
								return userInfoResponse.data;
							}
							return undefined;
						} catch (error: any) {
							Logger.error("User info failed", error);
							return undefined;
						}
					})();
				},
				login(email: string, password: string, account: any) {
					return flow(function* () {
						try {
							self.clear(SessionState.pendingAuth);
							const loginResponse: AxiosResponse<LoginInfo> = yield API.login(
								Config.getApiUrl("session", LOGIN_URL),
								{
									email: email,
									password: password,
									accountId: account?.id
								}
							);
							if (loginResponse.status === 200) {
								sessionStorage.setItem(AUTH_HEADER_ITEM, loginResponse.data.tokenType + " " + loginResponse.data.token);
								yield self.init();
								const isAuth = !!sessionStorage.getItem(SESSION_INFO_ITEM) && !!sessionStorage.getItem(AUTH_HEADER_ITEM);
								isAuthAfterGlow.set(isAuth);
								isAuthenticated.set(isAuth);
								isAuth && setTimeout(() => isAuthAfterGlow.set(false), 2000);
							} else {
								self.setState(SessionState.close);
								Logger.error("Authentication failed");
								throw new Error("Authentication failed");
							}
						} catch (error: any) {
							self.setState(SessionState.close);
							Logger.error("Login failed", error);
						}
					})();
				},
				logout() {
					return flow(function* () {
						try {
							yield API.post(Config.getApiUrl("session", LOGOUT_URL), {});
						} catch (error: any) {
							Logger.error("Logout failed", error);
						} finally {
							self.clear(SessionState.close);
							sessionStorage.removeItem(SESSION_INFO_ITEM);
							sessionStorage.removeItem(AUTH_HEADER_ITEM);
							isAuthenticated.set(false);
							window.location.replace("/");
						}
					})();
				}
			}
		};
	})
	.views((self) => ({
		get isKernelTenant(): boolean {
			return self.sessionInfo?.tenant.tenantType.id === KERNEL_TENANT;
		},
		get isAdvisorTenant(): boolean {
			return self.sessionInfo?.tenant.tenantType.id === ADVISOR_TENANT;
		},
		get isCommunityTenant(): boolean {
			return self.sessionInfo?.tenant.tenantType.id === COMMUNITY_TENANT;
		},
	}))
	.views((self) => ({
		get isUser(): boolean {
			return ["readOnly", "user", "super_user"].indexOf(self.sessionInfo?.user?.role!) >= 0;
		},
		get hasReadOnlyRole(): boolean {
			return "readOnly" === self.sessionInfo?.user?.role;
		},
		get hasUserRole(): boolean {
			return "user" === self.sessionInfo?.user?.role;
		},
		get hasSuperUserRole(): boolean {
			return "super_user" === self.sessionInfo?.user?.role;
		},
		get isAdmin(): boolean {
			return ["admin", "app_admin"].indexOf(self.sessionInfo?.user?.role!) >= 0;
		},
		get hasAdminRole(): boolean {
			return "admin" === self.sessionInfo?.user?.role;
		},
		get hasAppAdminRole(): boolean {
			return "app_admin" === self.sessionInfo?.user?.role;
		},
	}))
	.views((self) => ({
		get translator(): Translator {
			return new TranslatorImpl(self.locale as Locale);
		},
		get formatter(): Formatter {
			return new FormatterImpl(self.locale as Locale);
		},
		get hasExternalAuthentication() {
			return !!self.sessionInfo && !!self.sessionInfo!.user.extlIdpUserId;
		}
	}));

type MstSessionType = typeof MstSessionModel;

export interface MstSession extends MstSessionType { }

export const SessionModel: MstSession = MstSessionModel;
export interface Session extends Instance<typeof SessionModel> { }
export type MstSessionSnapshot = SnapshotIn<typeof MstSessionModel>;
export interface SessionSnapshot extends MstSessionSnapshot { }
