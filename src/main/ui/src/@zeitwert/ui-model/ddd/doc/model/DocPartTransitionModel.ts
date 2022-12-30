import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { UserInfo } from "../../../app/session";
import { CaseStage } from "./BpmModel";
import { DocPartModel } from "./DocPartModel";

const MstDocPartTransitionModel = DocPartModel.named("DocTransition").props({
	seqNr: types.maybe(types.number),
	user: types.maybe(types.frozen<UserInfo>()),
	modifiedAt: types.maybe(faTypes.date),
	//
	oldCaseStage: types.maybe(types.frozen<CaseStage>()),
	newCaseStage: types.maybe(types.frozen<CaseStage>()),
	changes: types.maybe(types.string)
});

type MstDocPartTransitionType = typeof MstDocPartTransitionModel;
interface MstDocPartTransition extends MstDocPartTransitionType { }

export const DocPartTransitionModel: MstDocPartTransition = MstDocPartTransitionModel;
export type DocPartTransitionModelType = typeof DocPartTransitionModel;
export interface DocPartTransition extends Instance<DocPartTransitionModelType> { }
export type DocPartTransitionSnapshot = SnapshotIn<DocPartTransitionModelType>;
export type DocPartTransitionPayload = Omit<DocPartTransitionSnapshot, "id">;
