import { IGNORED_ATTRIBUTES } from "../../../aggregate/service/impl/AggregateApiImpl";
import { ItemPartApiImpl } from "../../../item/service/impl/ItemPartApiImpl";
import { ObjPartTransitionModel, ObjPartTransitionSnapshot } from "../../model/ObjPartTransitionModel";
import { ObjPartTransitionApi } from "../ObjPartTransitionApi";

const MODULE = "obj";
const PATH = "objs";
const PART_PATH = "transitions";
const TYPE = "objTransition";
const INCLUDES = "include[objTransition]=obj";

export class ObjPartTransitionApiImpl
	extends ItemPartApiImpl<ObjPartTransitionSnapshot>
	implements ObjPartTransitionApi {
	constructor() {
		const PROPS = Object.keys(ObjPartTransitionModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { obj: "obj" };
		super(MODULE, PATH, PART_PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
