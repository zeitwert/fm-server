
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { UserInfo } from "../../../app/session";
import { Document } from "../../../fm/dms/model/DocumentModel";
import { AggregateModel } from "../../aggregate/model/AggregateModel";
import { Enumerated } from "../../aggregate/model/EnumeratedModel";
import { ObjMeta } from "./ObjMeta";

export enum GenericUserType {
	User = "user",
	Contact = "contact"
}

export type GenericUser = Enumerated | UserInfo;

const MstObjModel = AggregateModel
	.named("Obj")
	.props({
		meta: types.maybe(types.frozen<ObjMeta>()),
		//
		//documents: types.optional(types.array(types.reference(types.late((): IAnyModelType => DocumentModel))), [])
	})
	.views((self) => ({
		isObj() {
			return true;
		}
	}))
	.actions((self) => ({
		setDocuments(documents: Document[]) {
			flow(function* () {
				//const store: ObjStore = getRoot(self);
				// yield asyncForEach(
				// 	documents,
				// 	async (document) => (await document.id) && store.loadDocument(document.id)
				// );
				// replace(self.documents, documents, (a, b) => a.id === b.id);
				// store.updateDocumentCount(self.documents.length);
			})();
		}
	}));

type MstObjType = typeof MstObjModel;
interface MstObj extends MstObjType { }

export const ObjModel: MstObj = MstObjModel;
export type ObjModelType = typeof ObjModel;
export interface Obj extends Instance<ObjModelType> { }
export type ObjSnapshot = SnapshotIn<ObjModelType>
export type ObjPayload = Omit<ObjSnapshot, "id">;
