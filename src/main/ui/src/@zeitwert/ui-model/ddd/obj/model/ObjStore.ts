
import { StoreWithTasksModel } from "@zeitwert/ui-model/fm/collaboration/model/StoreWithTasks";
import Logger from "loglevel";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { session } from "../../../../ui-model/app/session";
import { EntityTypeRepository, requireThis } from "../../../app/common";
import { StoreWithNotesModel } from "../../../fm/collaboration/model/StoreWithNotes";
import { AggregateStoreModel } from "../../aggregate/model/AggregateStore";
import { Obj } from "./ObjModel";
import { StoreWithObjsModel } from "./StoreWithObjs";

const MstObjStoreModel = AggregateStoreModel
	.named("ObjStore")
	.props({
		objsStore: types.optional(StoreWithObjsModel, {}),
		notesStore: types.optional(StoreWithNotesModel, {}),
		tasksStore: types.optional(StoreWithTasksModel, {}),
	})
	.views((self) => ({
		get item(): Obj | undefined {
			requireThis(false, "item() is implemented");
			return undefined as unknown as Obj;
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
			self.objsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		delete() {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<void, any[]>(function* (): any {
				try {
					session.startNetwork();
					yield self.api.deleteAggregate(self.item!.id);
				} catch (error: any) {
					Logger.error("Failed to delete item", error);
					return Promise.reject(error);
				} finally {
					session.stopNetwork();
				}
			})();
		},
		async updateDocuments() {
			//await Promise.all(self.item!.documents.map((doc: Document) => DOCUMENT_API.storeItem(doc.apiSnapshot)));
		}
	}));

type MstObjStoreType = typeof MstObjStoreModel;
interface MstObjStore extends MstObjStoreType { }

export const ObjStoreModel: MstObjStore = MstObjStoreModel;
export type ObjStoreModelType = typeof ObjStoreModel;
export interface ObjStore extends Instance<ObjStoreModelType> { }
export type ObjStoreSnapshot = SnapshotIn<ObjStoreModelType>;
export type ObjStorePayload = Omit<ObjStoreSnapshot, "id">;
