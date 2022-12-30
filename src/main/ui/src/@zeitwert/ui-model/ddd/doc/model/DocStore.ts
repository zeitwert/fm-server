import Logger from "loglevel";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository, requireThis } from "../../../app/common";
import { Account } from "../../../fm/account/model/AccountModel";
import { StoreWithAccountsModel } from "../../../fm/account/model/StoreWithAccounts";
import { Document } from "../../../fm/dms/model/DocumentModel";
import { DOCUMENT_API } from "../../../fm/dms/service/DocumentApi";
import { AggregateStoreModel } from "../../aggregate/model/AggregateStore";
import { Obj } from "../../obj/model/ObjModel";
import { StoreWithObjsModel } from "../../obj/model/StoreWithObjs";
import { DOC_API } from "../service/DocApi";
import { CaseStage } from "./BpmModel";
import { Doc, DocModel, DocSnapshot } from "./DocModel";
import { DocPartTransition } from "./DocPartTransitionModel";
import { DocPartTransitionStoreModel } from "./DocPartTransitionStore";
import { StoreWithDocsModel } from "./StoreWithDocs";

const MstDocStoreModel = types
	.compose(AggregateStoreModel, DocPartTransitionStoreModel)
	.named("DocStore")
	.props({
		docsStore: types.optional(StoreWithDocsModel, {}),
		objsStore: types.optional(StoreWithObjsModel, {}),
		accountsStore: types.optional(StoreWithAccountsModel, {}),
	})
	.views((self) => ({
		get item(): Doc | undefined {
			requireThis(false, "item() is implemented");
			return undefined as unknown as Doc;
		},
		get stageTransitions(): DocPartTransition[] {
			return self.transitions
				.filter((transition) => transition.oldCaseStage !== undefined && transition.newCaseStage !== undefined)
				.sort((t1, t2) => (t1.modifiedAt! < t2.modifiedAt! ? 1 : -1));
		}
	}))
	.actions((self) => ({
		// lifecycle
		transitionTo(stage: CaseStage) {
			if (!self.isInTrx) {
				self.startTrx();
			}
			self.item!.caseStage = stage;
			return self.store();
		}
	}))
	.actions((self) => ({
		updateDocuments() {
			return flow(function* () {
				const promises: Promise<any>[] = [];
				self.item!.documents.forEach((doc: Document) => promises.push(DOCUMENT_API.storeAggregate(doc.apiSnapshot)));
				yield Promise.all(promises);
			})();
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.docsStore.afterLoad(repository);
			self.objsStore.afterLoad(repository);
			self.accountsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		findByRefObj(refObj: Obj, params?: any): Promise<Doc[]> {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<Doc[], any[]>(function* () {
				try {
					const docs: any = yield DOC_API.findByRefObj(refObj, params);
					return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
				} catch (error: any) {
					Logger.error("Failed to find docs by ref obj", error);
					return Promise.reject(error);
				}
			})();
		},
		findByRefDoc(refDoc: Doc, params?: any): Promise<Doc[]> {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<Doc[], any[]>(function* () {
				try {
					const docs: any = yield DOC_API.findByRefDoc(refDoc, params);
					return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
				} catch (error: any) {
					Logger.error("Failed to find docs by ref doc", error);
					return Promise.reject(error);
				}
			})();
		},
		findByAccount(account: Account): Promise<Doc[]> {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<Doc[], any[]>(function* () {
				try {
					const docs: any = yield DOC_API.findByAccount(account);
					return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
				} catch (error: any) {
					Logger.error("Failed to find docs by account", error);
					return Promise.reject(error);
				}
			})();
		},
		findUpcomingTasks(account: Account): Promise<Doc[]> {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<Doc[], any[]>(function* () {
				try {
					const docs: any = yield DOC_API.findUpcomingTasks(account);
					return docs.map((doc: DocSnapshot) => DocModel.create(doc)) || [];
				} catch (error: any) {
					Logger.error("Failed to find upcoming tasks", error);
					return Promise.reject(error);
				}
			})();
		}
	}));

type MstDocStoreType = typeof MstDocStoreModel;
interface MstDocStore extends MstDocStoreType { }

export const DocStoreModel: MstDocStore = MstDocStoreModel;
export type DocStoreModelType = typeof DocStoreModel;
export interface DocStore extends Instance<DocStoreModelType> { }
export type DocStoreSnapshot = SnapshotIn<DocStoreModelType>;
export type DocStorePayload = Omit<DocStoreSnapshot, "id">;
