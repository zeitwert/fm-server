
import Logger from "loglevel";
import { flow, Instance, SnapshotIn } from "mobx-state-tree";
import { session } from "../../../../ui-model/app/session";
import { requireThis } from "../../../app/common";
import { AggregateStoreModel } from "../../aggregate/model/AggregateStore";
import { Obj } from "./ObjModel";

const MstObjStoreModel = AggregateStoreModel
	.named("ObjStore")
	.props({
	})
	.views((self) => ({
		get item(): Obj | undefined {
			requireThis(false, "item() is implemented");
			return undefined as unknown as Obj;
		}
	}))
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
			//await Promise.all(self.item!.documents.map((doc: Document) => DOCUMENT_API.storeItem(getSnapshot(doc))));
		}
	}));

type MstObjStoreType = typeof MstObjStoreModel;
interface MstObjStore extends MstObjStoreType { }

export const ObjStoreModel: MstObjStore = MstObjStoreModel;
export type ObjStoreModelType = typeof ObjStoreModel;
export interface ObjStore extends Instance<ObjStoreModelType> { }
export type ObjStoreSnapshot = SnapshotIn<ObjStoreModelType>;
export type ObjStorePayload = Omit<ObjStoreSnapshot, "id">;
