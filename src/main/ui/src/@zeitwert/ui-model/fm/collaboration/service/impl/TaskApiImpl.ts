import moment from "moment";
import { API, Config, convertJsonApiToJson } from "../../../../app/common";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { TaskModel, TaskSnapshot } from "../../model/TaskModel";
import { TaskApi } from "../TaskApi";

const MODULE = "collaboration";
const PATH = "tasks";
const TYPE = "task";
const INCLUDES = "";

export class TaskApiImpl extends AggregateApiImpl<TaskSnapshot> implements TaskApi {

	constructor() {
		const PROPS = Object.keys(TaskModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["isInWork"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { /*account: "account"*/ };
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async findUpcomingTasks(size: number): Promise<TaskSnapshot[]> {
		const date = moment().toISOString();
		const suffix =
			"?page[limit]=" +
			size +
			"&" +
			INCLUDES +
			"&filter[doc][isInWork]=true&filter[task][dueDate][GE]=" +
			date +
			"&sort[task]=-dueDate";
		const response = await API.get(Config.getApiUrl(MODULE, PATH) + suffix);
		return convertJsonApiToJson(response.data);
	}

}
