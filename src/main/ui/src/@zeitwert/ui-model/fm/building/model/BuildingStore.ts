
import { applySnapshot, cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { StoreWithDocumentsModel } from "../../dms/model/StoreWithDocuments";
import { BuildingApi, BUILDING_API } from "../service/BuildingApi";
import { Building, BuildingModel, BuildingModelType, BuildingSnapshot } from "./BuildingModel";

const MstBuildingStoreModel = ObjStoreModel.named("BuildingStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		documentsStore: types.optional(StoreWithDocumentsModel, {}),
		building: types.maybe(BuildingModel)
	})
	.views((self) => ({
		get model(): BuildingModelType {
			return BuildingModel;
		},
		get api(): BuildingApi {
			return BUILDING_API;
		},
		get item(): Building | undefined {
			return self.building;
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.accountsStore.afterLoad(repository);
			self.documentsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(snapshot: BuildingSnapshot | undefined) {
			if (self.building) {
				applySnapshot(self.building, snapshot);
			} else {
				self.building = cast(snapshot);
			}
		}
	}));

type MstBuildingStoreType = typeof MstBuildingStoreModel;
interface MstBuildingStore extends MstBuildingStoreType { }

export const BuildingStoreModel: MstBuildingStore = MstBuildingStoreModel;
export type BuildingStoreModelType = typeof BuildingStoreModel;
export interface BuildingStore extends Instance<BuildingStoreModelType> { }
export type BuildingStoreSnapshot = SnapshotIn<BuildingStoreModelType>;
export type BuildingStorePayload = Omit<BuildingStoreSnapshot, "id">;
