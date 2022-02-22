import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { UserInfo } from "../../../app/session";
import { ObjPartModel } from "./ObjPartModel";

const MstObjPartTransitionModel = ObjPartModel.named("ObjTransition").props({
	seqNr: types.maybe(types.number),
	user: types.maybe(types.frozen<UserInfo>()),
	modifiedAt: types.maybe(faTypes.date),
	//
	changes: types.maybe(types.string)
});

type MstObjPartTransitionType = typeof MstObjPartTransitionModel;
export interface MstObjPartTransition extends MstObjPartTransitionType { }
export const ObjPartTransitionModel: MstObjPartTransition = MstObjPartTransitionModel;
export interface ObjPartTransition extends Instance<typeof ObjPartTransitionModel> { }
export type MstObjPartTransitionSnapshot = SnapshotIn<typeof MstObjPartTransitionModel>;
export interface ObjPartTransitionSnapshot extends MstObjPartTransitionSnapshot { }
export type ObjPartTransitionPayload = Omit<ObjPartTransitionSnapshot, "id">;
