
import { EntityTypeRepository } from "@zeitwert/ui-model/app";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithUsersModel } from "../../user/model/StoreWithUsers";
import { TENANT_API } from "../service/TenantApi";
import { Tenant, TenantModel, TenantSnapshot } from "./TenantModel";

const MstTenantStoreModel = ObjStoreModel
	.named("TenantStore")
	.props({
		usersStore: types.optional(StoreWithUsersModel, {}),
		tenant: types.maybe(TenantModel)
	})
	.views((self) => ({
		get model() {
			return TenantModel;
		},
		get api() {
			return TENANT_API;
		},
		get item(): Tenant | undefined {
			return self.tenant;
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.usersStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(snapshot: TenantSnapshot) {
			self.tenant = cast(snapshot);
		}
	}));

type MstTenantStoreType = typeof MstTenantStoreModel;
export interface MstTenantStore extends MstTenantStoreType { }
export const TenantStoreModel: MstTenantStore = MstTenantStoreModel;
export interface TenantStore extends Instance<typeof TenantStoreModel> { }
export type MstTenantStoreSnapshot = SnapshotIn<typeof MstTenantStoreModel>;
export interface TenantStoreSnapshot extends MstTenantStoreSnapshot { }
