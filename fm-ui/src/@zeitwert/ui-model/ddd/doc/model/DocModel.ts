
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Document } from "../../../fm/dms/model/DocumentModel";
import { AggregateModel } from "../../aggregate/model/AggregateModel";
import { Enumerated } from "../../aggregate/model/EnumeratedModel";
import { CaseStage } from "./BpmModel";
import { DocMeta } from "./DocMeta";

const MstDocModel = AggregateModel.named("Doc")
	.props({
		caption: types.optional(types.string, ""),
		//
		meta: types.maybe(types.frozen<DocMeta>()),
		//
		assignee: types.maybe(types.frozen<Enumerated>()), // to set new assignee
		caseDef: types.maybe(types.frozen<Enumerated>()), // to set case def on creation
		caseStage: types.maybe(types.frozen<CaseStage>()), // to set new case stage in transition
		//
		//documents: types.optional(types.array(types.reference(types.late((): IAnyModelType => DocumentModel))), [])
	})
	.views((self) => ({
		get isDoc(): boolean {
			return true;
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
	// 	async function setField(field: string, value: any) {
	// 		switch (field) {
	// 			case "account": {
	// 				return setAccount(value);
	// 			}
	// 			default: {
	// 				return superSetField(field, value);
	// 			}
	// 		}
	// 	}
	// 	return {
	// 		setAccount,
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
interface MstDoc extends MstDocType { }

export const DocModel: MstDoc = MstDocModel;
export type DocModelType = typeof DocModel;
export interface Doc extends Instance<DocModelType> { }
export type DocSnapshot = SnapshotIn<DocModelType>;
export type DocPayload = Omit<DocSnapshot, "id">;
