
import { API, Config, convertJsonApiToJson } from "../../../../app/common";
import { Account } from "../../../../fm/account/model/AccountModel";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../aggregate/service/impl/AggregateApiImpl";
import { DocModel, DocSnapshot } from "../../model/DocModel";
import { DocApi } from "../DocApi";

const MODULE = "doc";
const PATH = "docs";
const TYPE = "doc";
const INCLUDES = "include[doc]=account&include[account]=mainContact,contacts";

export class DocApiImpl extends AggregateApiImpl<DocSnapshot> implements DocApi {
	constructor() {
		const PROPS = Object.keys(DocModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["isInWork"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { account: "account" };
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async findByAccount(account: Account): Promise<DocSnapshot[]> {
		const suffix = "?filter[account.id]=" + account.id;
		const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
		return convertJsonApiToJson(response.data);
	}

	async findUpcomingTasks(account: Account): Promise<DocSnapshot[]> {
		const suffix = "?filter[account.id]=" + account.id + "&filter[isInWork]=true&filter[docTypeId]=doc_task";
		const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
		return convertJsonApiToJson(response.data);
	}
}
