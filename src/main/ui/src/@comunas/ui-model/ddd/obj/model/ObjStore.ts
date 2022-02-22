import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository, requireThis } from "../../../app/common";
import { AggregateStoreModel } from "../../aggregate/model/AggregateStore";
import { Obj } from "./ObjModel";
import { StoreWithObjsModel } from "./StoreWithObjs";

const MstObjStoreModel = AggregateStoreModel.named("ObjStore")
	.props({
		objsStore: types.optional(StoreWithObjsModel, {}),
	})
	.views((self) => ({
		get item(): Obj | undefined {
			requireThis(false, "item() is implemented");
			return undefined as unknown as Obj;
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.objsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		async updateDocuments() {
			//await Promise.all(self.item!.documents.map((doc: Document) => DOCUMENT_API.storeItem(doc.apiSnapshot)));
		}
	}));

type MstObjStoreType = typeof MstObjStoreModel;
export interface MstObjStore extends MstObjStoreType { }
export const ObjStoreModel: MstObjStore = MstObjStoreModel;
export interface ObjStore extends Instance<typeof ObjStoreModel> { }
export type MstObjStoreSnapshot = SnapshotIn<typeof MstObjStoreModel>;
export interface ObjStoreSnapshot extends MstObjStoreSnapshot { }
