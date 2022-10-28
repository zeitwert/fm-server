
import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";

export interface TenantStatistics {
}

const MstTenantModel = ObjModel.named("Tenant")
	.props({
		key: types.maybe(types.string),
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		//
		tenantType: types.maybe(types.frozen<Enumerated>()),
	})
	.views((self) => ({
		get formSnapshot(): TenantSnapshot {
			return toJS(getSnapshot(self));
		}
	}));

type MstTenantType = typeof MstTenantModel;
export interface MstTenant extends MstTenantType { }
export const TenantModel: MstTenant = MstTenantModel;
export interface Tenant extends Instance<typeof TenantModel> { }
export type TenantSnapshot = SnapshotIn<typeof MstTenantModel>;
export type TenantPayload = Omit<TenantSnapshot, "id">;
