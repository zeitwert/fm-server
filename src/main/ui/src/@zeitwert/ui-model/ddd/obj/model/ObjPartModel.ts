
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { PartModel } from "../../part/model/PartModel";
import { ObjModel } from "./ObjModel";

const MstObjPartModel = PartModel.named("ObjPart").props({
	obj: types.maybe(types.reference(ObjModel))
});

type MstObjPartType = typeof MstObjPartModel;
interface MstObjPart extends MstObjPartType { }

export const ObjPartModel: MstObjPart = MstObjPartModel;
export type ObjPartModelType = typeof ObjPartModel;
export interface ObjPart extends Instance<ObjPartModelType> { }
export type ObjPartSnapshot = SnapshotIn<ObjPartModelType>;
export type ObjPartPayload = Omit<ObjPartSnapshot, "id">;
