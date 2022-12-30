
import { AxiosResponse } from "axios";
import { Canvg, presets } from "canvg";
import AppBanner from "frame/ui/AppBannerSvg";
import Logger from "loglevel";
import { observable, reaction, transaction } from "mobx";
import { addDisposer, applySnapshot, flow, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
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
import { LoginInfo, LoginTenantInfo, LoginUserInfo } from "./LoginInfo";
import { ADVISOR_TENANT, COMMUNITY_TENANT, KERNEL_TENANT, SessionInfo } from "./SessionInfo";
import userPrefs, { UserPrefs } from "./UserPrefs";

export enum SessionState {
	close = "close",
	pendingAuth = "pendingAuth",
	pendingOpen = "pendingOpen",
	open = "open",
	pendingClose = "pendingClose"
}

const USER_INFO_URL = "userInfo";
const TENANT_INFO_URL = "tenantInfo";

const LOGIN_URL = "login";
const LOGOUT_URL = "logout";

const SESSION_URL = "session";
const APP_LIST_URL = "applications";

const preset = presets.offscreen();

const MstSessionModel = types
	.model("Session", {
		state: types.optional(types.string, sessionStorage.getItem(SESSION_STATE_ITEM) || SessionState.close),
		locale: types.maybe(types.enumeration((Languages as string[]).concat(Locales as string[]))),
		sessionInfo: types.maybe(types.frozen<SessionInfo>()),
		bannerUrl: types.optional(types.string, ""),
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
			if (self.state === SessionState.close) {
				sessionStorage.removeItem(SESSION_INFO_ITEM);
				sessionStorage.removeItem(AUTH_HEADER_ITEM);
			}
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
					const appResponse = yield API.get(Config.getRestUrl("app", APP_LIST_URL + "/" + appId));
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
					self.setState(SessionState.close);
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
						const sessionResponse: AxiosResponse<SessionInfo> = yield API.get(Config.getRestUrl("session", SESSION_URL));
						sessionInfo = sessionResponse.data;
						sessionStorage.setItem(SESSION_INFO_ITEM, JSON.stringify(sessionInfo));
					} else {
						sessionInfo = oldSessionInfo;
					}
					const appListResponse = yield API.get(Config.getRestUrl("app", APP_LIST_URL));
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
				userInfo(email: string): Promise<LoginUserInfo | undefined> {
					return flow(function* () {
						try {
							const userInfoResponse: AxiosResponse<LoginUserInfo> = yield API.get(Config.getRestUrl("app", USER_INFO_URL + "/" + email));
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
				tenantInfo(id: string): Promise<LoginTenantInfo | undefined> {
					return flow(function* () {
						try {
							const tenantInfoResponse: AxiosResponse<LoginTenantInfo> = yield API.get(Config.getRestUrl("app", TENANT_INFO_URL + "/" + id));
							if (tenantInfoResponse.status === 200) {
								return tenantInfoResponse.data;
							}
							return undefined;
						} catch (error: any) {
							Logger.error("Tenant info failed", error);
							return undefined;
						}
					})();
				},
				login(email: string, password: string, tenant: any, account: any) {
					return flow(function* () {
						try {
							self.clear(SessionState.pendingAuth);
							const loginResponse: AxiosResponse<LoginInfo> = yield API.login(
								Config.getRestUrl("session", LOGIN_URL),
								{
									email: email,
									password: password,
									tenantId: tenant?.id,
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
							yield API.post(Config.getRestUrl("session", LOGOUT_URL), {});
						} catch (error: any) {
							Logger.error("Logout failed", error);
						} finally {
							self.clear(SessionState.close);
							isAuthenticated.set(false);
							window.location.replace("/");
						}
					})();
				}
			}
		};
	})
	.actions(self => {
		return {
			afterCreate() {
				addDisposer(self, reaction(
					() => {
						return {
							sessionInfo: self.sessionInfo
						};
					},
					async () => {
						let logoUrl = "";
						let title = "";
						let subTitle = "";
						if (!self.appInfo?.id) {
							self.bannerUrl = "/zw-banner.jpg";
						} else {
							if (self.sessionInfo?.account) {
								logoUrl = !!self.sessionInfo?.account.logo?.contentType ? Config.getRestUrl("account", "accounts/" + self.sessionInfo?.account.id + "/logo") : "";
								title = self.sessionInfo?.account.caption;
								subTitle = self.sessionInfo.tenant.caption;
							} else if (self.sessionInfo) {
								logoUrl = !!self.sessionInfo.tenant.logo?.contentType ? Config.getRestUrl("oe", "tenants/" + self.sessionInfo.tenant.id + "/logo") : "";
								title = self.sessionInfo.tenant.caption;
								subTitle = self.sessionInfo.tenant.tenantType.name;
							}
							let svg = AppBanner
								.replace("{title}", title)
								.replace("{subTitle}", subTitle);
							if (!!logoUrl) {
								svg = svg.replace("{logo}", logoUrl);
							} else {
								svg = svg.replace("{logo}", "")
									.replace("<text x=\"50\"", "<text x=\"5\"")
									.replace("<text x=\"51\"", "<text x=\"5\"");
							}
							return flow(function* () {
								try {
									const canvas = new OffscreenCanvas(300, 50);
									const ctx = canvas.getContext("2d")!;
									const v = yield Canvg.from(ctx, svg, preset);
									yield v.render(); // render only first frame, ignoring animations and mouse.
									const blob = yield canvas.convertToBlob();
									self.bannerUrl = URL.createObjectURL(blob);
								} catch (error: any) {
									Logger.error("Failed to get metadata form definition");
									return Promise.reject(error);
								}
							})();
						}
					}
				));
			}
		}
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
			return ["read_only", "user", "super_user"].indexOf(self.sessionInfo?.user?.role?.id!) >= 0;
		},
		get hasReadOnlyRole(): boolean {
			return "read_only" === self.sessionInfo?.user?.role?.id;
		},
		get hasUserRole(): boolean {
			return "user" === self.sessionInfo?.user?.role?.id;
		},
		get hasSuperUserRole(): boolean {
			return "super_user" === self.sessionInfo?.user?.role?.id;
		},
		get isAdmin(): boolean {
			return ["admin", "app_admin"].indexOf(self.sessionInfo?.user?.role?.id!) >= 0;
		},
		get hasAdminRole(): boolean {
			return "admin" === self.sessionInfo?.user?.role?.id;
		},
		get hasAppAdminRole(): boolean {
			return "app_admin" === self.sessionInfo?.user?.role?.id;
		},
	}))
	.views((self) => ({
		get userPrefs(): UserPrefs {
			return userPrefs;
		},
		get translator(): Translator {
			return new TranslatorImpl(self.locale as Locale);
		},
		get formatter(): Formatter {
			return new FormatterImpl(self.locale as Locale);
		},
		get hasExternalAuthentication() {
			return !!self.sessionInfo && false /*!!self.sessionInfo!.user.extlIdpUserId*/;
		}
	}))
	.views((self) => ({
		avatarUrl(userId: string | undefined): string {
			return userId ? Config.getRestUrl("oe", `users/${userId}/avatar`) : "/assets/images/avatar1.jpg";
		},
	}));

type MstSessionType = typeof MstSessionModel;
interface MstSession extends MstSessionType { }

export const SessionModel: MstSession = MstSessionModel;
export type SessionModelType = typeof SessionModel;
export interface Session extends Instance<SessionModelType> { }
export type SessionSnapshot = SnapshotIn<SessionModelType>;
export type SessionPayload = Omit<SessionSnapshot, "id">;
