
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { AccountModel, AccountSnapshot } from "../../model/AccountModel";
import { AccountApi } from "../AccountApi";

const MODULE = "account";
const PATH = "accounts";
const TYPE = "account";
const INCLUDES = "include[account]=tenantInfo,contacts,mainContact,logo";

export class AccountApiImpl extends AggregateApiImpl<AccountSnapshot> implements AccountApi {
	constructor() {
		const PROPS = Object.keys(AccountModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["contacts", "refObj", "documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			logo: "document",
			mainContact: "contact",
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
