
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { TENANT_API } from "../service/TenantApi";
import { Tenant, TenantModel, TenantSnapshot } from "./TenantModel";

const MstStoreWithTenantsModel = StoreWithEntitiesModel.named("StoreWithTenants")
	.props({
		tenants: types.optional(types.map(types.late(() => TenantModel)), {})
	})
	.views((self) => ({
		getTenant(id: string): Tenant | undefined {
			return self.tenants.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["tenant"], self.tenants);
		}
	}))
	.actions((self) => ({
		async loadTenant(id: string): Promise<Tenant> {
			const tenant = self.getTenant(id);
			if (tenant) {
				return tenant;
			}
			await self.loadEntity<Tenant, TenantSnapshot>(id, TENANT_API);
			return self.getTenant(id)!;
		}
	}));

type MstStoreWithTenantsType = typeof MstStoreWithTenantsModel;
export interface MstStoreWithTenants extends MstStoreWithTenantsType { }
export const StoreWithTenantsModel: MstStoreWithTenants = MstStoreWithTenantsModel;
export interface StoreWithTenants extends Instance<typeof StoreWithTenantsModel> { }
export type MstStoreWithTenantsSnapshot = SnapshotIn<typeof MstStoreWithTenantsModel>;
export interface StoreWithTenantsSnapshot extends MstStoreWithTenantsSnapshot { }
