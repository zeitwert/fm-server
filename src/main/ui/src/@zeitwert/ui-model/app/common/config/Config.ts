
export type HttpMethod = "get" | "delete" | "head" | "post" | "put" | "patch";

export const Config = {
	getEnvParam(param: string): string {
		return process.env["REACT_APP_" + param]!;
	},
	getEnumUrl(module: string, enumName: string) {
		return this.getEnvParam("API_BASE_URL")
			.replace("{type}", "enum")
			.replace("{api}", module + "/" + enumName);
	},
	getApiUrl(module: string, url: string) {
		return this.getEnvParam("API_BASE_URL")
			.replace("{type}", "api")
			.replace("{api}", module + "/" + url);
	},
	getRestUrl(module: string, url: string) {
		return this.getEnvParam("API_BASE_URL")
			.replace("{type}", "rest")
			.replace("{api}", module + "/" + url);
	},
	getProjectionUrl(module: string, url: string) {
		return this.getEnvParam("SERVER_BASE_URL")
			.replace("{type}", "rest")
			.replace("{api}", module + "/" + url);
	},
	getDocUrl(ctx: string) {
		return this.getEnvParam("DOC_BASE_URL")
			.replace("{ctx}", ctx ? "#" + ctx : "");
	},
	getTenantConfigUrl(tenant: string, url: string) {
		return this.getEnvParam("API_BASE_URL")
			.replace("{type}", "config")
			.replace("{api}", tenant + "/" + url);
	},
	getModuleConfigUrl(tenant: string, module: string, url: string) {
		return this.getEnvParam("API_BASE_URL")
			.replace("{type}", "config")
			.replace("{api}", tenant + "/" + module + "/" + url);
	},
	getMockUrl(tenant: string, module: string, url: string) {
		return this.getEnvParam("API_BASE_URL")
			.replace("{type}", "mock")
			.replace("{api}", tenant + "/" + module + "/" + url);
	}
};
