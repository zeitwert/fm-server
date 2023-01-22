import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { BuildingModel, BuildingSnapshot } from "../../model/BuildingModel";
import { BuildingApi } from "../BuildingApi";

const MODULE = "building";
const PATH = "buildings";
const TYPE = "building";
const INCLUDES = "include[building]=account,contacts,coverFoto";

export class BuildingApiImpl extends AggregateApiImpl<BuildingSnapshot> implements BuildingApi {
	constructor() {
		const PROPS = Object.keys(BuildingModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["refObj", "documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			account: "account",
			contacts: "contact",
			coverFoto: "document",
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
