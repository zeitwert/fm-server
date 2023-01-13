
import { Config } from "@zeitwert/ui-model/app";
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { ContactModel } from "../../contact/model/ContactModel";
import { DocumentModel } from "../../dms/model/DocumentModel";
import { TenantModel } from "../../tenant/model/TenantModel";

export interface AccountStatistics {
}

const MstAccountModel = ObjModel.named("Account")
	.props({
		key: types.maybe(types.string),
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		//
		accountType: types.maybe(types.frozen<Enumerated>()),
		clientSegment: types.maybe(types.frozen<Enumerated>()),
		areas: types.optional(types.array(types.frozen<Enumerated>()), []),
		referenceCurrency: types.maybe(types.frozen<Enumerated>()),
		tenantInfo: types.maybe(types.reference(TenantModel)),
		inflationRate: types.maybe(types.number),
		//
		contacts: types.optional(types.array(types.reference(ContactModel)), []),
		mainContact: types.maybe(types.reference(ContactModel)),
		//
		logo: types.maybe(types.reference(DocumentModel)),
		//
		statistics: types.frozen<AccountStatistics>()
	})
	.views((self) => ({
		get hasLogo(): boolean {
			return !!self.logo?.id && !!self.logo?.contentType?.id;
		},
		get logoUrl(): string | undefined {
			return Config.getRestUrl("account", "accounts/" + self.id + "/logo");
		},
	}));

type MstAccountType = typeof MstAccountModel;
interface MstAccount extends MstAccountType { }

export const AccountModel: MstAccount = MstAccountModel;
export type AccountModelType = typeof AccountModel;
export interface Account extends Instance<AccountModelType> { }
export type AccountSnapshot = SnapshotIn<AccountModelType>;
export type AccountPayload = Omit<AccountSnapshot, "id">;
