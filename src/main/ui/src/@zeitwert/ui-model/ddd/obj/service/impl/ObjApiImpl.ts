import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../aggregate/service/impl/AggregateApiImpl";
import { ObjModel, ObjSnapshot } from "../../model/ObjModel";
import { ObjApi } from "../ObjApi";

const MODULE = "obj";
const PATH = "objs";
const TYPE = "obj";
const INCLUDES = "";

export class ObjApiImpl extends AggregateApiImpl<ObjSnapshot> implements ObjApi {
	constructor() {
		const PROPS = Object.keys(ObjModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { refObj: "obj" };
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
