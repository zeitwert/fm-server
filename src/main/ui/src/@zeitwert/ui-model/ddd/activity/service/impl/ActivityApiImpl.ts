import { API, Config, EntityTypeRepository } from "../../../../app/common";
import { UserInfo } from "../../../../app/session";
import { Aggregate } from "../../../../ddd/aggregate/model/AggregateModel";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../aggregate/service/impl/AggregateApiImpl";
import { ActivityModel, ActivitySnapshot } from "../../model/ActivityModel";
import { ActivityApi } from "../ActivityApi";

const MODULE = "activity";
const PATH = "activities";
const TYPE = "activity";
const INCLUDES = "include[activity]=refDoc,refObj,assignee,account,contact";

export class ActivityApiImpl extends AggregateApiImpl<ActivitySnapshot> implements ActivityApi {

	constructor() {
		const PROPS = Object.keys(ActivityModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["isInWork"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { refObj: "obj", refDoc: "doc", assignee: "obj", account: "account", contact: "contact" };
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async findByUser(user: UserInfo): Promise<EntityTypeRepository> {
		const filter = {
			OR: [{ ownerId: user.id }, { assigneeId: user.id }]
		};
		const suffix = "?" + INCLUDES + "&filter=" + JSON.stringify(filter);
		const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
		return this.deserializeData(response.data);
	}

	async findByItem(item: Aggregate): Promise<EntityTypeRepository> {
		const suffix = "?" + INCLUDES + this.getFilterParams(item);
		const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
		return this.deserializeData(response.data);
	}

	private getFilterParams(item: Aggregate) {
		const params = {};
		// switch (item.type.type) {
		// 	case EntityType.CONTACT:
		// 		params["contact.id"] = item.id;
		// 		break;
		// 	case EntityType.ACCOUNT:
		// 		params["account.id"] = item.id;
		// 		break;
		// 	default:
		// 		if (item.isObj) {
		// 			params["refObj.id"] = item.id;
		// 		} else if (item.isDoc) {
		// 			params["refDoc.id"] = item.id;
		// 		}
		// 		break;
		// }
		return this.parseFilterParams(params);
	}
}
