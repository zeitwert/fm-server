
import { IAnyModelType, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityType } from "../../../app/common";
import { AccountModel } from "../../../fm/account/model/AccountModel";
import { Document, DocumentModel } from "../../../fm/dms/model/DocumentModel";
import { AggregateModel } from "../../aggregate/model/AggregateModel";
import { ObjModel } from "../../obj/model/ObjModel";
import { CaseStage } from "./BpmModel";
import { DocMeta } from "./DocMeta";

const MstDocModel = AggregateModel.named("Doc")
	.props({
		caption: types.optional(types.string, ""),
		//
		meta: types.maybe(types.frozen<DocMeta>()),
		isInWork: types.optional(types.boolean, false),
		caseStage: types.maybe(types.frozen<CaseStage>()),
		//
		assignee: types.maybe(types.reference(types.late((): IAnyModelType => ObjModel))),
		//
		account: types.maybe(types.reference(AccountModel)),
		// refObj: types.maybe(types.reference(ObjModel)),
		// refDoc: types.maybe(types.reference(types.late((): IAnyModelType => DocModel))),
		//
		documents: types.optional(types.array(types.reference(types.late((): IAnyModelType => DocumentModel))), [])
	})
	.views((self) => ({
		get isDoc(): boolean {
			return true;
		},
		get isBusinessProcess() {
			return (
				self.type.type === EntityType.LEAD
			);
		},
		isActionAvailable(action: string) {
			return self.meta?.availableActions?.indexOf(action)! >= 0 || false;
		}
	}))
	// .actions((self) => {
	// 	const superSetField = self.setField;
	// 	async function setAccount(id: string) {
	// 		id && (await (self.rootStore as DocStore).accountsStore.loadAccount(id));
	// 		superSetField("account", id);
	// 	}
	// 	async function setRefDoc(id: string) {
	// 		id && (await (self.rootStore as DocStore).docsStore.loadDoc(id));
	// 		superSetField("refDoc", id);
	// 	}
	// 	async function setRefObj(id: string) {
	// 		id && (await (self.rootStore as DocStore).objsStore.loadObj(id));
	// 		superSetField("refObj", id);
	// 	}
	// 	async function setField(field: string, value: any) {
	// 		switch (field) {
	// 			case "account": {
	// 				return setAccount(value);
	// 			}
	// 			case "refDoc": {
	// 				return setRefDoc(value);
	// 			}
	// 			case "refObj": {
	// 				return setRefObj(value);
	// 			}
	// 			default: {
	// 				return superSetField(field, value);
	// 			}
	// 		}
	// 	}
	// 	return {
	// 		setAccount,
	// 		setRefDoc,
	// 		setRefObj,
	// 		setField
	// 	};
	// })
	.actions((self) => ({
		setDocuments(documents: Document[]) {
			// flow(function* () {
			// const store: DocStore = getRoot(self);
			// yield asyncForEach(
			// 	documents,
			// 	async (document) => (await document.id) && store.loadDocument(document.id)
			// );
			// replace(self.documents, documents, (a, b) => a.id === b.id);
			// store.updateDocumentCount(self.documents.length);
			// })();
		}
	}));

type MstDocType = typeof MstDocModel;
export interface MstDoc extends MstDocType { }

export const DocModel: MstDoc = MstDocModel;
export type DocModelType = typeof DocModel;
export interface Doc extends Instance<DocModelType> { }
export type DocSnapshot = SnapshotIn<DocModelType>;
export type DocPayload = Omit<DocSnapshot, "id">;
