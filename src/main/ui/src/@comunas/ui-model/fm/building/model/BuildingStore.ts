import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { BUILDING_API } from "../service/BuildingApi";
import { Building, BuildingModel } from "./BuildingModel";

const MstBuildingStoreModel = ObjStoreModel.named("BuildingStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		building: types.maybe(BuildingModel)
	})
	.views((self) => ({
		get model() {
			return BuildingModel;
		},
		get api() {
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
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(item: Building) {
			self.building = item;
		}
	}));

type MstBuildingStoreType = typeof MstBuildingStoreModel;
export interface MstBuildingStore extends MstBuildingStoreType { }
export const BuildingStoreModel: MstBuildingStore = MstBuildingStoreModel;
export interface BuildingStore extends Instance<typeof BuildingStoreModel> { }
export type MstBuildingStoreSnapshot = SnapshotIn<typeof MstBuildingStoreModel>;
export interface BuildingStoreSnapshot extends MstBuildingStoreSnapshot { }
