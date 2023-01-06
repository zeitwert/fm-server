
import { EntityTypeRepository } from "@zeitwert/ui-model/app";
import { transaction } from "mobx";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithDocumentsModel } from "../../dms/model/StoreWithDocuments";
import { TenantApi, TENANT_API } from "../service/TenantApi";
import { Tenant, TenantModel, TenantModelType, TenantSnapshot } from "./TenantModel";

const MstTenantStoreModel = ObjStoreModel
	.named("TenantStore")
	.props({
		documentsStore: types.optional(StoreWithDocumentsModel, {}),
		tenant: types.maybe(TenantModel)
	})
	.views((self) => ({
		get model(): TenantModelType {
			return TenantModel;
		},
		get api(): TenantApi {
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
			transaction(() => {
				self.tenant = cast({ id: snapshot.id } as TenantSnapshot);
				self.tenant = cast(snapshot);
			});
		}
	}));

type MstTenantStoreType = typeof MstTenantStoreModel;
interface MstTenantStore extends MstTenantStoreType { }

export const TenantStoreModel: MstTenantStore = MstTenantStoreModel;
export type TenantStoreModelType = typeof TenantStoreModel;
export interface TenantStore extends Instance<TenantStoreModelType> { }
export type TenantStoreSnapshot = SnapshotIn<TenantStoreModelType>;
export type TenantStorePayload = Omit<TenantStoreSnapshot, "id">;
