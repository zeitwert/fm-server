
import { EntityTypeRepository } from "@zeitwert/ui-model/app";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithDocumentsModel } from "../../dms/model/StoreWithDocuments";
import { StoreWithTenantsModel } from "../../tenant/model/StoreWithTenants";
import { USER_API } from "../service/UserApi";
import { User, UserModel, UserSnapshot } from "./UserModel";

const MstUserStoreModel = ObjStoreModel
	.named("UserStore")
	.props({
		tenantsStore: types.optional(StoreWithTenantsModel, {}),
		documentsStore: types.optional(StoreWithDocumentsModel, {}),
		user: types.maybe(UserModel)
	})
	.views((self) => ({
		get model() {
			return UserModel;
		},
		get api() {
			return USER_API;
		},
		get item(): User | undefined {
			return self.user;
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.tenantsStore.afterLoad(repository);
			self.documentsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(snapshot: UserSnapshot) {
			self.user = cast(snapshot);
		}
	}));

type MstUserStoreType = typeof MstUserStoreModel;
export interface MstUserStore extends MstUserStoreType { }
export const UserStoreModel: MstUserStore = MstUserStoreModel;
export interface UserStore extends Instance<typeof UserStoreModel> { }
export type MstUserStoreSnapshot = SnapshotIn<typeof MstUserStoreModel>;
export interface UserStoreSnapshot extends MstUserStoreSnapshot { }
