import { Env } from "./Env";

export type HttpMethod = "get" | "delete" | "head" | "post" | "put" | "patch";

export const Config = {
	getEnumUrl(module: string, enumName: string) {
		return Env.getParam("API_BASE_URL")
			.replace("{type}", "enum")
			.replace("{api}", module + "/" + enumName);
	},
	getApiUrl(module: string, url: string) {
		return Env.getParam("API_BASE_URL")
			.replace("{type}", "api")
			.replace("{api}", module + "/" + url);
	},
	getRestUrl(module: string, url: string) {
		return Env.getParam("API_BASE_URL")
			.replace("{type}", "rest")
			.replace("{api}", module + "/" + url);
	},
	getTenantConfigUrl(tenant: string, url: string) {
		return Env.getParam("API_BASE_URL")
			.replace("{type}", "config")
			.replace("{api}", tenant + "/" + url);
	},
	getModuleConfigUrl(tenant: string, module: string, url: string) {
		return Env.getParam("API_BASE_URL")
			.replace("{type}", "config")
			.replace("{api}", tenant + "/" + module + "/" + url);
	},
	getMockUrl(tenant: string, module: string, url: string) {
		return Env.getParam("API_BASE_URL")
			.replace("{type}", "mock")
			.replace("{api}", tenant + "/" + module + "/" + url);
	}
};
