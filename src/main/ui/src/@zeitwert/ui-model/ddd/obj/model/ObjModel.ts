
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { UserInfo } from "../../../app/session";
import { Contact } from "../../../fm/contact/model/ContactModel";
import { Document } from "../../../fm/dms/model/DocumentModel";
import { AggregateModel } from "../../aggregate/model/AggregateModel";
import { ObjMeta } from "./ObjMeta";

export enum GenericUserType {
	User = "user",
	Contact = "contact"
}

export type GenericUser = Contact | UserInfo;

const MstObjModel = AggregateModel
	.named("Obj")
	.props({
		meta: types.maybe(types.frozen<ObjMeta>()),
		//
		//refObj: types.maybe(types.reference(types.late((): IAnyModelType => ObjModel))),
		//
		//documents: types.optional(types.array(types.reference(types.late((): IAnyModelType => DocumentModel))), [])
	})
	.views((self) => ({
		isObj() {
			return true;
		}
	}))
	// .actions((self) => {
	// 	const superSetField = self.setField;
	// async function setRefObj(id: string) {
	// 	id && (await (self.rootStore as ObjStore).objsStore.loadObj(id));
	// 	superSetField("refObj", id);
	// }
	// async function setField(field: string, value: any) {
	// 	switch (field) {
	// 		case "refObj": {
	// 			return setRefObj(value);
	// 		}
	// 		default: {
	// 			return superSetField(field, value);
	// 		}
	// 	}
	// }
	// return {
	// setRefObj,
	// 		setField
	// 	};
	// })
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
export interface MstObj extends MstObjType { }
export const ObjModel: MstObj = MstObjModel;
export interface Obj extends Instance<typeof ObjModel> { }
export type MstObjSnapshot = SnapshotIn<typeof ObjModel>;
export interface ObjSnapshot extends MstObjSnapshot { }
export type ObjPayload = Omit<ObjSnapshot, "id">;
