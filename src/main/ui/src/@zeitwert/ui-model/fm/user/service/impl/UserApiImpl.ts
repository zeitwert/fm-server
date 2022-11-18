
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { UserModel, UserSnapshot } from "../../model/UserModel";
import { UserApi } from "../UserApi";

const MODULE = "oe";
const PATH = "users";
const TYPE = "user";
const INCLUDES = "include[user]=avatar";

export class UserApiImpl extends AggregateApiImpl<UserSnapshot> implements UserApi {
	constructor() {
		const PROPS = Object.keys(UserModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			tenant: "tenant",
			avatar: "document",
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
