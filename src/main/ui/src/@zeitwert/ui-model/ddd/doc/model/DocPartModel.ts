
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { PartModel } from "../../part/model/PartModel";
import { DocModel } from "./DocModel";

const MstDocPartModel = PartModel.named("DocPart").props({
	doc: types.maybe(types.reference(DocModel))
});

type MstDocPartType = typeof MstDocPartModel;
interface MstDocPart extends MstDocPartType { }

export const DocPartModel: MstDocPart = MstDocPartModel;
export type DocPartModelType = typeof DocPartModel;
export interface DocPart extends Instance<DocPartModelType> { }
export type DocPartSnapshot = SnapshotIn<DocPartModelType>;
export type DocPartPayload = Omit<DocPartSnapshot, "id">;
