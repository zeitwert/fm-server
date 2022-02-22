import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { PartModel } from "../../part/model/PartModel";
import { ObjModel } from "./ObjModel";

const MstObjPartModel = PartModel.named("ObjPart").props({
	obj: types.maybe(types.reference(ObjModel))
});

type MstObjPartType = typeof MstObjPartModel;
export interface MstObjPart extends MstObjPartType { }
export const ObjPartModel: MstObjPart = MstObjPartModel;
export interface ObjPart extends Instance<typeof ObjPartModel> { }
export type MstObjPartSnapshot = SnapshotIn<typeof ObjPartModel>;
export interface ObjPartSnapshot extends MstObjPartSnapshot { }
export type ObjPartPayload = Omit<ObjPartSnapshot, "id">;
