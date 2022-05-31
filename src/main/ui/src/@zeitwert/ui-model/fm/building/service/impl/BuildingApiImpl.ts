import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { BuildingModel, BuildingSnapshot } from "../../model/BuildingModel";
import { BuildingApi } from "../BuildingApi";

const MODULE = "building";
const PATH = "buildings";
const TYPE = "building";
const INCLUDES = "include[building]=account,coverFoto";

export class BuildingApiImpl extends AggregateApiImpl<BuildingSnapshot> implements BuildingApi {
	constructor() {
		const PROPS = Object.keys(BuildingModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["refObj", "documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			//refObj: "obj",
			//documents: "document",
			account: "account",
			coverFoto: "document",
			building_manager: "contact",
			portfolio_manager: "contact"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
