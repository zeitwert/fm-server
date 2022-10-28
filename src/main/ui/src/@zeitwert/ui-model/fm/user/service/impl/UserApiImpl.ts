
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { UserModel, UserSnapshot } from "../../model/UserModel";
import { UserApi } from "../UserApi";

const MODULE = "oe";
const PATH = "users";
const TYPE = "user";
const INCLUDES = "";

export class UserApiImpl extends AggregateApiImpl<UserSnapshot> implements UserApi {
	constructor() {
		const PROPS = Object.keys(UserModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			//tenant: "tenant",
			//documents: "document",
			//mainContact: "contact",
			//holdings: "holding"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
