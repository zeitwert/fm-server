
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { TenantModel, TenantSnapshot } from "../../model/TenantModel";
import { TenantApi } from "../TenantApi";

const MODULE = "oe";
const PATH = "tenants";
const TYPE = "tenant";
const INCLUDES = "";

export class TenantApiImpl extends AggregateApiImpl<TenantSnapshot> implements TenantApi {
	constructor() {
		const PROPS = Object.keys(TenantModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			//refObj: "obj",
			//documents: "document",
			//mainContact: "contact",
			//holdings: "holding"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
