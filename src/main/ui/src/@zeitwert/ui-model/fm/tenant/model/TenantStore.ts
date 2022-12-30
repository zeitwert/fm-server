
import { EntityTypeRepository } from "@zeitwert/ui-model/app";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithDocumentsModel } from "../../dms/model/StoreWithDocuments";
import { TENANT_API } from "../service/TenantApi";
import { Tenant, TenantModel, TenantSnapshot } from "./TenantModel";

const MstTenantStoreModel = ObjStoreModel
	.named("TenantStore")
	.props({
		documentsStore: types.optional(StoreWithDocumentsModel, {}),
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
			self.documentsStore.afterLoad(repository);
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
export type TenantStoreModelType = typeof TenantStoreModel;
export interface TenantStore extends Instance<TenantStoreModelType> { }
export type TenantStoreSnapshot = SnapshotIn<TenantStoreModelType>;
export type TenantStorePayload = Omit<TenantStoreSnapshot, "id">;
