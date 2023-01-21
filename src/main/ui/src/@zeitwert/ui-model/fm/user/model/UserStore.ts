
import { applySnapshot, cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../../ui-model/app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithDocumentsModel } from "../../dms/model/StoreWithDocuments";
import { StoreWithTenantsModel } from "../../tenant/model/StoreWithTenants";
import { UserApi, USER_API } from "../service/UserApi";
import { User, UserModel, UserModelType, UserSnapshot } from "./UserModel";

const MstUserStoreModel = ObjStoreModel
	.named("UserStore")
	.props({
		tenantsStore: types.optional(StoreWithTenantsModel, {}),
		documentsStore: types.optional(StoreWithDocumentsModel, {}),
		user: types.maybe(UserModel)
	})
	.views((self) => ({
		get model(): UserModelType {
			return UserModel;
		},
		get api(): UserApi {
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
		setItem(snapshot: UserSnapshot | undefined) {
			if (self.user) {
				applySnapshot(self.user, snapshot);
			} else {
				self.user = cast(snapshot);
			}
		}
	}));

type MstUserStoreType = typeof MstUserStoreModel;
interface MstUserStore extends MstUserStoreType { }

export const UserStoreModel: MstUserStore = MstUserStoreModel;
export type UserStoreModelType = typeof UserStoreModel;
export interface UserStore extends Instance<UserStoreModelType> { }
export type UserStoreSnapshot = SnapshotIn<UserStoreModelType>;
export type UserStorePayload = Omit<UserStoreSnapshot, "id">;
