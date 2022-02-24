import { advancedConvertJsonApiToJson, API, Config, DateFormat, transform } from "../../../common";
import { Datamart } from "../../model/Datamart";
import { Layout } from "../../model/Layout";
import { Provider } from "../../model/Provider";

const STD_FUNCTIONS = {
	optional(path: any, key: string, def: string): string {
		return path && path[key] ? path[key] : def;
	},
	optional2(path: any, key: string, key2: string, def: string): string {
		return path ? (path[key] ? path[key] : path[key2]) : def;
	},
	substr(s: string, start: number, length?: number): string {
		return s?.substring(start, length);
	},
	join(items: any[]): string {
		if (items && Array.isArray(items)) {
			if (items[0] && items[0] instanceof Object && items[0].hasOwnProperty("caption")) {
				return items.map((item) => item.caption).join(", ");
			} else if (items[0] && items[0] instanceof Object && items[0].hasOwnProperty("name")) {
				return items.map((item) => item.name).join(", ");
			}
			return items.join(", ");
		}
		return items;
	},
	formatDate(date: any, withTime: true): string {
		if (date) {
			return DateFormat.short(date, withTime);
		}
		return "";
	}
};

export class ApiProvider implements Provider {
	get id() {
		return "api";
	}

	get name() {
		return "zeitwert Api Server";
	}

	async list(
		datamart: Datamart,
		layout: Layout,
		params?: { [key: string]: any },
		sort?: string,
		limit?: number
	): Promise<any> {
		datamart.validateValues(params!);
		let param = !params
			? {}
			: Object.keys(params)
				.filter((p) => !datamart.params[p]?.isIntl)
				.reduce((q, p, i) => {
					const key = datamart.params[p]?.name || p;
					const value = params[p];
					return q + (i > 0 ? "&" : "") + "filter[" + key + "]=" + value;
				}, "");
		if (sort) {
			param += (param !== "" ? "&" : "") + "sort=" + sort;
		}
		if (limit) {
			param += (param !== "" ? "&" : "") + "page[limit]=" + limit;
		}
		const baseUrl = datamart.config.isMock
			? Config.getMockUrl("t1", datamart.config.module, datamart.config.url)
			: Config.getApiUrl(datamart.config.module, datamart.config.url);
		const url = baseUrl + (!!param ? (baseUrl.indexOf("?") >= 0 ? "&" : "?") + param : "");
		return await API.get(url);
	}

	async execute(
		datamart: Datamart,
		layout: Layout,
		params?: { [key: string]: any },
		sort?: string,
		limit?: number
	): Promise<any> {
		const list = await this.list(datamart, layout, params, sort, limit);
		const meta = Object.assign({}, list.data.meta);
		list.data = !datamart.config.isMock
			? await advancedConvertJsonApiToJson(list.data, datamart.config.module)
			: list.data;
		return Object.assign(transform(list, 0, layout.layout, STD_FUNCTIONS), {
			meta: meta
		});
	}
}
