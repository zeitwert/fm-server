import { AggregateModel, AggregateSnapshot } from "../../../aggregate/model/AggregateModel";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../aggregate/service/impl/AggregateApiImpl";
import { ItemApi } from "../ItemApi";

// const LIMIT = 10;

// const SEARCHABLE_TYPES = [
// 	"obj_contact",
// 	"obj_account",
// 	"doc_lead",
// 	"doc_task"
// ];

const MODULE = "item";
const PATH = "items";
const TYPE = "item";
const INCLUDES = "";

export class ItemApiImpl extends AggregateApiImpl<AggregateSnapshot> implements ItemApi {

	constructor() {
		const PROPS = Object.keys(AggregateModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	// async getCounters(item: Aggregate): Promise<AggregateCounters> {
	// 	const response = await API.get(Config.getApiUrl(MODULE, "counters") + "/" + item.id);
	// 	return response.data;
	// }

	// async changeOwner(item: Aggregate, user: UserInfo) {
	// 	const response = await API.post(Config.getApiUrl(MODULE, "changeOwner") + "/" + item.id + "/" + user.id);
	// 	return response.data;
	// }

	// async getRecentByUser(user: UserInfo): Promise<EntityRepository> {
	// 	let suffix = "?filter[itemTypeId][IN]=" + SEARCHABLE_TYPES.join(",");
	// 	suffix += "&filter[modifiedByUserId][EQ]=" + user.id;
	// 	suffix += "&sort=-modifiedAt,-id&page[limit]=" + LIMIT;
	// 	const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
	// 	return this.deserializeData(response.data);
	// }

	// async getFrequentByUser(user: UserInfo): Promise<EntityRepository> {
	// 	let suffix = "?filter[itemTypeId][IN]=" + SEARCHABLE_TYPES.join(",");
	// 	suffix += "&filter[modifiedByUserId][EQ]=" + user.id;
	// 	suffix += "&sort=-touches,-id&page[limit]=" + LIMIT;
	// 	const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
	// 	return this.deserializeData(response.data);
	// }

}
