
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { USER_API } from "../service/UserApi";
import { User, UserModel, UserSnapshot } from "./UserModel";

const MstStoreWithUsersModel = StoreWithEntitiesModel.named("StoreWithUsers")
	.props({
		users: types.optional(types.map(types.late(() => UserModel)), {})
	})
	.views((self) => ({
		getUser(id: string): User | undefined {
			return self.users.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["user"], self.users);
		}
	}))
	.actions((self) => ({
		async loadUser(id: string): Promise<User> {
			const user = self.getUser(id);
			if (user) {
				return user;
			}
			await self.loadEntity<User, UserSnapshot>(id, USER_API);
			return self.getUser(id)!;
		}
	}));

type MstStoreWithUsersType = typeof MstStoreWithUsersModel;
export interface MstStoreWithUsers extends MstStoreWithUsersType { }
export const StoreWithUsersModel: MstStoreWithUsers = MstStoreWithUsersModel;
export interface StoreWithUsers extends Instance<typeof StoreWithUsersModel> { }
export type MstStoreWithUsersSnapshot = SnapshotIn<typeof MstStoreWithUsersModel>;
export interface StoreWithUsersSnapshot extends MstStoreWithUsersSnapshot { }
