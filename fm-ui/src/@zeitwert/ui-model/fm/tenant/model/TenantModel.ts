
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Config } from "../../../../ui-model/app/common/config/Config";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { DocumentModel } from "../../dms/model/DocumentModel";

const MstTenantModel = ObjModel.named("Tenant")
	.props({
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		inflationRate: types.maybe(types.number),
		discountRate: types.maybe(types.number),
		//
		tenantType: types.maybe(types.frozen<Enumerated>()),
		//
		logo: types.maybe(types.reference(DocumentModel)),
	})
	.views((self) => ({
		get hasLogo(): boolean {
			return !!self.logo?.id && !!self.logo?.contentType?.id;
		},
		get logoUrl(): string | undefined {
			return Config.getRestUrl("dms", "documents/" + self.logo?.id + "/content");
		},
	}));

type MstTenantType = typeof MstTenantModel;
interface MstTenant extends MstTenantType { }

export const TenantModel: MstTenant = MstTenantModel;
export type TenantModelType = typeof TenantModel;
export interface Tenant extends Instance<TenantModelType> { }
export type TenantSnapshot = SnapshotIn<TenantModelType>;
export type TenantPayload = Omit<TenantSnapshot, "id">;
