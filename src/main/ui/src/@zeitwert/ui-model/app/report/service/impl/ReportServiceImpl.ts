import { API, assertThis, Config } from "../../../common";
import { Datamart } from "../../model/Datamart";
import { DatamartImpl } from "../../model/impl/DatamartImpl";
import { LayoutImpl } from "../../model/impl/LayoutImpl";
import { TemplateImpl } from "../../model/impl/TemplateImpl";
import { Layout } from "../../model/Layout";
import { Provider } from "../../model/Provider";
import { Template } from "../../model/Template";
import { ReportService } from "../ReportService";
import { ApiProvider } from "./ApiProvider";

const ProviderMap: Map<string, Provider> = new Map();
ProviderMap.set("api", new ApiProvider());
ProviderMap.set("mock", new ApiProvider());

const Datamarts: Datamart[] = [];
const DatamartMap: Map<string, Datamart> = new Map();
let DatamartsPromise: Promise<Datamart[]>;

const LayoutsMap: Map<string, Layout[]> = new Map(); // datamartId => layouts
const LayoutMap: Map<string, Layout> = new Map();
const LayoutsPromiseMap: Map<string, Promise<Layout[]>> = new Map(); // datamartId => Promise<layouts>

const TemplatesMap: Map<string, Template[]> = new Map(); // datamartId => templates
const TemplateMap: Map<string, Template> = new Map();
const TemplatesPromiseMap: Map<string, Promise<Template[]>> = new Map(); // datamartId => Promise<templates>

export class ReportServiceImpl implements ReportService {
	provider(id: string): Provider {
		return ProviderMap[id];
	}

	async datamarts(): Promise<Datamart[]> {
		if (Datamarts.length > 0) {
			return Datamarts;
		} else if (!DatamartsPromise) {
			return (DatamartsPromise = this.loadDatamarts());
		}
		return DatamartsPromise;
	}

	private async loadDatamarts(): Promise<Datamart[]> {
		const response = await API.get<any[]>(Config.getTenantConfigUrl("t1", "datamarts"));
		response.data.forEach((item) => {
			assertThis(!!item.provider, `datamart ${item.id} defines a provider`);
			const provider = ProviderMap.get(item.provider);
			assertThis(!!provider, `provider ${item.provider} must be available`);
			const dm: Datamart = new DatamartImpl(item.id, item.name, provider!, item.params, item.config);
			Datamarts.push(dm);
			DatamartMap.set(dm.id, dm);
		});
		return Datamarts;
	}

	async datamart(id: string): Promise<Datamart | undefined> {
		await this.datamarts();
		return DatamartMap.get(id);
	}

	async layouts(datamartId: string): Promise<Layout[]> {
		return await this.datamarts().then(() => {
			if (LayoutsMap.has(datamartId)) {
				return LayoutsMap.get(datamartId)!;
			} else if (!LayoutsPromiseMap.has(datamartId)) {
				LayoutsPromiseMap.set(datamartId, this.loadLayouts(datamartId));
			}
			return LayoutsPromiseMap.get(datamartId)!;
		});
	}

	private async loadLayouts(datamartId: string): Promise<Layout[]> {
		const [module, datamart] = datamartId.split(".");
		const dm = await this.datamart(datamartId)!;
		const url = Config.getModuleConfigUrl("t1", module, "datamarts/" + datamart + "/layouts");
		const response = await API.get<any[]>(url);
		LayoutsMap.set(datamartId, []);
		response.data.forEach((item) => {
			const layout: Layout = new LayoutImpl(dm!, datamartId + "." + item.id, item.name, item.layout);
			LayoutsMap.get(datamartId)!.push(layout);
			LayoutMap.set(layout.id, layout);
		});
		return LayoutsMap.get(datamartId)!;
	}

	async layout(id: string): Promise<Layout | undefined> {
		const [module, datamart] = id.split(".");
		await this.layouts(module + "." + datamart);
		return LayoutMap.get(id);
	}

	async templates(datamartId: string): Promise<Template[]> {
		return await this.layouts(datamartId).then(() => {
			if (TemplatesMap.has(datamartId)) {
				return TemplatesMap.get(datamartId)!;
			} else if (!TemplatesPromiseMap.has(datamartId)) {
				TemplatesPromiseMap.set(datamartId, this.loadTemplates(datamartId));
			}
			return TemplatesPromiseMap.get(datamartId)!;
		});
	}

	private async loadTemplates(datamartId: string): Promise<Template[]> {
		try {
			const [module, datamart] = datamartId.split(".");
			const dm = await this.datamart(datamartId);
			const url = Config.getModuleConfigUrl("t1", module, "datamarts/" + datamart + "/templates");
			const response = await API.get<any[]>(url);
			TemplatesMap.set(datamartId, []);
			response.data.forEach((item) => {
				const layout = LayoutMap.get(datamartId + "." + item.layout)!;
				const template = new TemplateImpl(
					dm!,
					layout!,
					datamartId + "." + item.id,
					item.name,
					item.params,
					item.sort,
					item.limit
				);
				TemplatesMap.get(datamartId)!.push(template);
				TemplateMap.set(template.id, template);
			});
			return TemplatesMap.get(datamartId)!;
		} catch (error: any) {
			console.error(error);
			return [];
		}
	}

	async template(id: string): Promise<Template | undefined> {
		const [module, datamart] = id.split(".");
		return await this.templates(module + "." + datamart).then(() => TemplateMap.get(id));
	}
}
