import normalize from "json-api-normalizer";
import { Deserializer, Serializer, SerializerOptions } from "jsonapi-serializer";
import { asyncForEach } from "../utils/Async";

/**
 * There are 3 possible JSON formats:
 *
 * **JsonApiObject** (JsonApi transport format)
 *
 * "data" can be a list of objects (list format) or a single object (object format)
 * {
 *   data: [
 *     {                    // JsonApiObject
 *       id: string,
 *       type: string,
 *       links: [
 *         self: string,
 *         next?: string,
 *         prev?: string,
 *         first?: string,
 *         last?: string
 *       ],
 *       attributes: [
 *         key: value
 *       ],
 *       relationships: [
 *         key: {
 *           data: {
 *             id: string,
 *             type: string
 *           },
 *           links: {
 *             self: string,
 *             related: string
 *           }
 *         }
 *       ]
 *     }
 *   ],
 *   included: [
 *     JsonApiObject
 *   ]
 * }
 *
 * **Repository** (used to interact with MST data stores)
 *
 * {
 *   "entityType": {
 *     "entityId": JsonObject (with relations by reference / id)
 *   }
 * }
 *
 * **JsonObject** (denormalized JSON object)
 *
 */

export interface SimpleJsonApiObj {
	id: string;
	type: string;
	data: any;
	links: any;
}

export interface IncludedJsonApiObj {
	id: string;
	type: string;
	attributes: any;
	relationships: any;
	links: any;
	meta: any;
}

export interface FullJsonApiObj {
	data: IncludedJsonApiObj[];
	included?: IncludedJsonApiObj[];
	links: any;
	meta: any;
}

export interface EntityRepository {
	[itemId: string]: any;
}

export interface EntityTypeRepository {
	[itemType: string]: EntityRepository;
}

const jsonApiDeserializer = new Deserializer({
	keyForAttribute: "camelCase"
});

export class JsonApiSerializer {
	jsonApiSerializer: Serializer;

	constructor(itemType: string, attributes: string[], relations: any) {
		const options: SerializerOptions = {
			attributes: attributes,
			pluralizeType: false,
			keyForAttribute: "camelCase",
			typeForAttribute: (attribute: string) => {
				if (relations[attribute]) {
					return relations[attribute];
				}
				return attribute;
			},
			dataMeta: function (record: any) { return record.meta; }
		};
		Object.keys(relations).forEach((key) => (options[key] = { ref: true }));
		this.jsonApiSerializer = new Serializer(itemType, options);
	}

	private async deserialize(inItem: any): Promise<any> {
		const outItem = await jsonApiDeserializer.deserialize({ data: inItem });
		if (inItem.relationships) {
			Object.keys(inItem.relationships).forEach((relName) => {
				const rel = inItem.relationships[relName].data;
				if (Array.isArray(rel)) {
					outItem[relName] = rel.map((r) => r.id);
				} else if (!!rel?.id) {
					// crnk sends "null" if empty 8-/
					outItem[relName] = rel.id;
				}
			});
		}
		return outItem;
	}

	public async convertJsonApiToRepository(obj: FullJsonApiObj): Promise<EntityRepository> {
		const json = normalize(cleanNulls(obj));
		const typeIds = Object.keys(json);
		const typeMap = {};
		await asyncForEach(typeIds, async (typeId) => {
			const items = json[typeId];
			const itemIds = Object.keys(items);
			const itemMap = {};
			await asyncForEach(itemIds, async (itemId) => (itemMap[itemId] = await this.deserialize(items[itemId])));
			typeMap[typeId] = itemMap;
		});
		return typeMap;
	}

	public convertObjToJsonApiObj(obj: any): SimpleJsonApiObj {
		const result = this.jsonApiSerializer.serialize(obj);
		return result;
	}
}

export function convertUndefinedToNull(obj: any) {
	Object.keys(obj).forEach((key) => {
		try {
			const prop = obj[key];
			// eslint-disable-next-line
			if (prop == undefined) {
				obj[key] = null;
			} else if (Array.isArray(prop)) {
				prop.forEach((item) => convertUndefinedToNull(item));
			} else if (typeof prop === "object") {
				obj[key] = Object.assign({}, convertUndefinedToNull(prop));
			}
		} catch (error: any) {
			console.error(error.message);
		}
	});
	return obj;
}

export function cleanNulls(obj: any) {
	Object.keys(obj).forEach((key) => {
		const prop = obj[key];
		if (prop == null) {
			delete obj[key];
		} else if (Array.isArray(prop)) {
			prop.forEach((item) => cleanNulls(item));
		} else if (typeof prop === "object") {
			cleanNulls(prop);
		}
	});
	return obj;
}

export async function convertJsonApiToJson(obj: FullJsonApiObj): Promise<any> {
	return await jsonApiDeserializer.deserialize(cleanNulls(obj));
}

/**
 * This deserializer uses the type of the obj to deserialize to properly convert to JSON
 * included relations to itself (crnk does not put them in included if they are in the list of obj to deserialize).
 *
 * @param obj
 * @param type
 * @returns
 */
export async function advancedConvertJsonApiToJson(obj: FullJsonApiObj, type: string): Promise<any> {
	const jsonApiRecursiveDeserializer = new Deserializer({
		keyForAttribute: "camelCase",
		[type]: {
			valueForRelationship: async (relationship: any, b: any, c: any) => {
				let obtained = relationship;
				(await asyncForEach(obj.data, async (item) => {
					if (item.id === relationship.id) {
						obtained = await convertJsonApiToJson({ data: item } as any);
					}
				})) as any;
				(await asyncForEach(obj.included || [], async (item) => {
					if (item.id === relationship.id) {
						obtained = await convertJsonApiToJson({ data: item } as any);
					}
				})) as any;
				return obtained;
			}
		}
	});
	return await jsonApiRecursiveDeserializer.deserialize(cleanNulls(obj));
}
