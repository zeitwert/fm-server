
import { API, API_HEADERS, Config, EntityTypeRepository, JsonApiSerializer } from "../../../../app/common";
import { AggregateSnapshot } from "../../model/AggregateModel";
import { AggregateApi } from "../AggregateApi";

export const IGNORED_ATTRIBUTES = [
	"meta",
	"caption",
	"sdsItems",
	"sdsObjs",
	"sdsDocs",
	"sdsAccounts",
	"sdsContacts",
	"sdsDocuments"
];

export abstract class AggregateApiImpl<S extends AggregateSnapshot> implements AggregateApi<S> {

	private readonly module: string;
	private readonly itemPath: string;
	private readonly includes: string;
	private readonly itemType: string;
	private jsonApiSerializer: JsonApiSerializer;

	protected constructor(
		module: string,
		itemPath: string,
		itemType: string,
		includes: string,
		attributes: string[],
		relations?: any
	) {
		this.module = module;
		this.itemPath = itemPath;
		this.itemType = itemType;
		this.includes = includes;
		this.jsonApiSerializer = new JsonApiSerializer(itemType, attributes, relations || []);
	}

	getModule() {
		return this.module;
	}

	getItemPath() {
		return this.itemPath;
	}

	getItemType() {
		return this.itemType;
	}

	protected deserializeData(data: any): Promise<EntityTypeRepository> {
		return this.jsonApiSerializer.convertJsonApiToRepository(data);
	}

	protected serializeItem(item: any) {
		return this.jsonApiSerializer.convertObjToJsonApiObj(item);
	}

	protected parseFilterParams(params: object) {
		return this.parseParams(params, "filter");
	}

	protected parseSortParams(params: object) {
		return this.parseParams(params, "sort");
	}

	private parseParams(params: object, prop: string) {
		return Object.keys(params).reduce((q, p, i) => {
			const key = params[p]?.name || p;
			const value = params[p];
			return "&" + prop + "[" + key + "]=" + value;
		}, "");
	}

	async getAggregates(parameters?: string): Promise<EntityTypeRepository> {
		const url = Config.getApiUrl(
			this.module,
			this.itemPath + (parameters ? "?" + parameters : "")
		);
		const response = await API.get(url);
		return this.deserializeData(response.data);
	}

	async loadAggregate(id: string, noIncludes?: boolean): Promise<EntityTypeRepository> {
		const url = Config.getApiUrl(
			this.module,
			this.itemPath + "/" + id + (!!this.includes && !noIncludes ? "?" + this.includes : "")
		);
		const response = await API.get(url);
		return this.deserializeData(response.data);
	}

	async createAggregate(item: S): Promise<EntityTypeRepository> {
		const url = Config.getApiUrl(this.module, this.itemPath + (!!this.includes ? "?" + this.includes : ""));
		const body = this.serializeItem(item);
		delete body.data.id;
		delete body.data.attributes.id;
		const response = await API.post(url, body, { headers: API_HEADERS });
		return this.deserializeData(response.data);
	}

	async storeAggregate(item: S): Promise<EntityTypeRepository> {
		const url = Config.getApiUrl(
			this.module,
			this.itemPath + "/" + item.id + (!!this.includes ? "?" + this.includes : "")
		);
		const body = this.serializeItem(item);
		delete body.data.attributes.id;
		const response = await API.patch(url, body, { headers: API_HEADERS });
		return this.deserializeData(response.data);
	}

	async deleteAggregate(id: string) {
		const url = Config.getApiUrl(
			this.module,
			this.itemPath + "/" + id
		);
		await API.delete(url, { headers: API_HEADERS });
	}

}
