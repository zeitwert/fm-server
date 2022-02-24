
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { PartModel } from "../../part/model/PartModel";
import { DocModel } from "./DocModel";

const MstDocPartModel = PartModel.named("DocPart").props({
	doc: types.maybe(types.reference(DocModel))
});

type MstDocPartType = typeof MstDocPartModel;
export interface MstDocPart extends MstDocPartType { }
export const DocPartModel: MstDocPart = MstDocPartModel;
export interface DocPart extends Instance<typeof DocPartModel> { }
export type MstDocPartSnapshot = SnapshotIn<typeof DocPartModel>;
export interface DocPartSnapshot extends MstDocPartSnapshot { }
export type DocPartPayload = Omit<DocPartSnapshot, "id">;
