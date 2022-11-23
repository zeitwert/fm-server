
import { Config } from "@zeitwert/ui-model/app";
import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { AccountModel } from "../../account/model/AccountModel";
import { DocumentModel } from "../../dms/model/DocumentModel";
import { UserModel } from "../../user/model/UserModel";

export interface TenantStatistics {
}

const MstTenantModel = ObjModel.named("Tenant")
	.props({
		key: types.maybe(types.string),
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		inflationRate: types.maybe(types.number),
		//
		tenantType: types.maybe(types.frozen<Enumerated>()),
		//
		users: types.optional(types.array(types.reference(UserModel)), []),
		accounts: types.optional(types.array(types.reference(AccountModel)), []),
		banner: types.maybe(types.reference(DocumentModel)),
		logo: types.maybe(types.reference(DocumentModel)),
	})
	.views((self) => ({
		get hasLogo(): boolean {
			return !!self.logo?.id && !!self.logo?.contentType?.id;
		},
		get logoUrl(): string | undefined {
			if (self.logo?.id && self.logo?.contentType?.id) {
				return Config.getRestUrl("dms", "documents/" + self.logo?.id + "/content");
			}
			return "/missing.jpg";
		},
	}))
	.views((self) => ({
		get hasBanner(): boolean {
			return !!self.banner?.id && !!self.banner?.contentType?.id;
		},
		get bannerUrl(): string | undefined {
			if (self.banner?.id && self.banner?.contentType?.id) {
				return Config.getRestUrl("dms", "documents/" + self.banner?.id + "/content");
			}
			return "/missing.jpg";
		},
	}))
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
