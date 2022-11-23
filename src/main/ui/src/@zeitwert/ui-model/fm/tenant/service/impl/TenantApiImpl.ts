
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { TenantModel, TenantSnapshot } from "../../model/TenantModel";
import { TenantApi } from "../TenantApi";

const MODULE = "oe";
const PATH = "tenants";
const TYPE = "tenant";
const INCLUDES = "include[tenant]=banner,logo,users,accounts";

export class TenantApiImpl extends AggregateApiImpl<TenantSnapshot> implements TenantApi {
	constructor() {
		const PROPS = Object.keys(TenantModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			banner: "document",
			logo: "document",
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
