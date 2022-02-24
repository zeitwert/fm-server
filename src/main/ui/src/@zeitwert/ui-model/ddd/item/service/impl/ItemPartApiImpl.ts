import { API, API_HEADERS, Config, EntityTypeRepository, JsonApiSerializer } from "../../../../app/common";
import { ItemPartSnapshot } from "../../model/ItemPartModel";
import { ItemPartApi } from "../ItemPartApi";

export abstract class ItemPartApiImpl<S extends ItemPartSnapshot> implements ItemPartApi<S> {
	private readonly module: string;
	private readonly itemPath: string;
	private readonly includes: string;
	private readonly itemType: string;
	private jsonApiSerializer: JsonApiSerializer;

	protected constructor(
		module: string,
		itemPath: string,
		partPath: string,
		itemType: string,
		includes: string,
		attributes: string[],
		relations?: any
	) {
		this.module = module;
		this.itemPath = itemPath + "/{{parentId}}/" + partPath;
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

	async loadParts(parentId: string, parentType: string): Promise<EntityTypeRepository> {
		const url = this.getApiUrl(
			parentId,
			this.module,
			this.itemPath + "?filter[" + parentType + ".id]=" + parentId + (!!this.includes ? "&" + this.includes : "")
		);
		const response = await API.get(url);
		return this.deserializeData(response.data);
	}

	async loadPart(parentId: string, id: string): Promise<EntityTypeRepository> {
		const url = this.getApiUrl(
			parentId,
			this.module,
			this.itemPath + "/" + id.split("-")[1] + (!!this.includes ? "?" + this.includes : "")
		);
		const response = await API.get(url);
		return this.deserializeData(response.data);
	}

	async addPart(part: S, parentId: string): Promise<EntityTypeRepository> {
		const url = this.getApiUrl(parentId, this.module, this.itemPath + (!!this.includes ? "?" + this.includes : ""));
		const body = this.serializeItem(
			Object.assign({}, part, {
				id: parentId
			})
		);
		delete body.data.attributes.id;
		const response = await API.post(url, body, { headers: API_HEADERS });
		return this.deserializeData(response.data);
	}

	async storePart(part: S, parentId: string): Promise<EntityTypeRepository> {
		const url = this.getApiUrl(
			parentId,
			this.module,
			this.itemPath + "/" + part.id!.split("-")[1] + (!!this.includes ? "?" + this.includes : "")
		);
		const body = this.serializeItem(part);
		delete body.data.attributes.id;
		const response = await API.patch(url, body, { headers: API_HEADERS });
		return this.deserializeData(response.data);
	}

	async removePart(part: S, parentId: string) {
		const url = this.getApiUrl(
			parentId,
			this.module,
			this.itemPath + "/" + part.id!.split("-")[1] + (!!this.includes ? "?" + this.includes : "")
		);
		await API.delete(url, { headers: API_HEADERS, data: this.serializeItem(part) });
	}

	getApiUrl(parentId: string, module: string, itemPath: string) {
		return Config.getApiUrl(module, itemPath.replace("{{parentId}}", parentId));
	}
}
