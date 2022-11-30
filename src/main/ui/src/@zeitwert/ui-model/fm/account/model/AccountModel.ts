
import { Config } from "@zeitwert/ui-model/app";
import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { ContactModel } from "../../contact/model/ContactModel";
import { DocumentModel } from "../../dms/model/DocumentModel";

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
	}))
	.views((self) => ({
		get mainContactSnapshot(): any {
			return self.mainContact ? toJS(getSnapshot(self.mainContact)) : undefined;
		},
		get contactsSnapshot(): any[] {
			return self.contacts
				.slice()
				.sort((c1) => (c1.id === self.mainContact?.id ? -1 : 1))
				.map((c) => c.formSnapshot);
		}
	}))
	.views((self) => ({
		get formSnapshot(): AccountSnapshot & {
			contactsInfo?: any[];
		} {
			return Object.assign({}, toJS(getSnapshot(self)), {
				contactsInfo: self.contactsSnapshot,
			});
		}
	}));

type MstAccountType = typeof MstAccountModel;
export interface MstAccount extends MstAccountType { }
export const AccountModel: MstAccount = MstAccountModel;
export interface Account extends Instance<typeof AccountModel> { }
export type AccountSnapshot = SnapshotIn<typeof MstAccountModel>;
export type AccountPayload = Omit<AccountSnapshot, "id">;
