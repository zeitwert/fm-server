
import { StoreWithNotesModel } from "@zeitwert/ui-model/fm/collaboration/model/StoreWithNotes";
import { StoreWithTasksModel } from "@zeitwert/ui-model/fm/collaboration/model/StoreWithTasks";
import { StoreWithContactsModel } from "@zeitwert/ui-model/fm/contact/model/StoreWithContacts";
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository, requireThis } from "../../../app/common";
import { StoreWithAccountsModel } from "../../../fm/account/model/StoreWithAccounts";
import { AggregateStoreModel } from "../../aggregate/model/AggregateStore";
import { CaseStage } from "./BpmModel";
import { Doc } from "./DocModel";

const MstDocStoreModel = AggregateStoreModel
	.named("DocStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		contactsStore: types.optional(StoreWithContactsModel, {}),
		notesStore: types.optional(StoreWithNotesModel, {}),
		tasksStore: types.optional(StoreWithTasksModel, {}),
	})
	.views((self) => ({
		get item(): Doc | undefined {
			requireThis(false, "item() is implemented");
			return undefined as unknown as Doc;
		},
	}))
	.actions((self) => ({
		// lifecycle
		transitionTo(stage: CaseStage) {
			if (!self.isInTrx) {
				self.startTrx();
			}
			self.item!.nextCaseStage = stage;
			return self.store();
		}
	}))
	.actions((self) => ({
		updateDocuments() {
			// return flow(function* () {
			// 	const promises: Promise<any>[] = [];
			// 	self.item!.documents.forEach((doc: Document) => promises.push(DOCUMENT_API.storeAggregate(doc.apiSnapshot)));
			// 	yield Promise.all(promises);
			// })();
		}
	}))
	.actions((self) => {
		const superLoad = self.load;
		const load = async (id: string) => {
			try {
				const item = await superLoad(id);
				await self.notesStore.loadNotes(id);
				await self.tasksStore.loadTasks(id);
				return item;
			} catch (error: any) {
				return Promise.reject(error);
			}
		}
		return { load };
	})
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.accountsStore.afterLoad(repository);
			self.contactsStore.afterLoad(repository);
		}
		return { afterLoad };
	});
// .actions((self) => ({
// 	findByRefObj(refObj: Obj, params?: any): Promise<Doc[]> {
// 		requireThis(!self.isInTrx, "not in transaction");
// 		return flow<Doc[], any[]>(function* () {
// 			try {
// 				const docs: any = yield DOC_API.findByRefObj(refObj, params);
// 				return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
// 			} catch (error: any) {
// 				Logger.error("Failed to find docs by ref obj", error);
// 				return Promise.reject(error);
// 			}
// 		})();
// 	},
// 	findByRefDoc(refDoc: Doc, params?: any): Promise<Doc[]> {
// 		requireThis(!self.isInTrx, "not in transaction");
// 		return flow<Doc[], any[]>(function* () {
// 			try {
// 				const docs: any = yield DOC_API.findByRefDoc(refDoc, params);
// 				return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
// 			} catch (error: any) {
// 				Logger.error("Failed to find docs by ref doc", error);
// 				return Promise.reject(error);
// 			}
// 		})();
// 	},
// 	findByAccount(account: Account): Promise<Doc[]> {
// 		requireThis(!self.isInTrx, "not in transaction");
// 		return flow<Doc[], any[]>(function* () {
// 			try {
// 				const docs: any = yield DOC_API.findByAccount(account);
// 				return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
// 			} catch (error: any) {
// 				Logger.error("Failed to find docs by account", error);
// 				return Promise.reject(error);
// 			}
// 		})();
// 	},
// 	findUpcomingTasks(account: Account): Promise<Doc[]> {
// 		requireThis(!self.isInTrx, "not in transaction");
// 		return flow<Doc[], any[]>(function* () {
// 			try {
// 				const docs: any = yield DOC_API.findUpcomingTasks(account);
// 				return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
// 			} catch (error: any) {
// 				Logger.error("Failed to find upcoming tasks", error);
// 				return Promise.reject(error);
// 			}
// 		})();
// 	}
// }));

type MstDocStoreType = typeof MstDocStoreModel;
interface MstDocStore extends MstDocStoreType { }

export const DocStoreModel: MstDocStore = MstDocStoreModel;
export type DocStoreModelType = typeof DocStoreModel;
export interface DocStore extends Instance<DocStoreModelType> { }
export type DocStoreSnapshot = SnapshotIn<DocStoreModelType>;
export type DocStorePayload = Omit<DocStoreSnapshot, "id">;
