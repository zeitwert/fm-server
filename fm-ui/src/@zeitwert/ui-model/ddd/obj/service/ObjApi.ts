import { AggregateApi } from "../../aggregate/service/AggregateApi";
import { ObjSnapshot } from "../model/ObjModel";
import { ObjApiImpl } from "./impl/ObjApiImpl";

export interface ObjApi extends AggregateApi<ObjSnapshot> { }

export const OBJ_API: ObjApi = new ObjApiImpl();
