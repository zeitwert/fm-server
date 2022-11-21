import { Config } from "@zeitwert/ui-model/app";
import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { ContactRole } from "../../contact/model/ContactEnums";
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
		banner: types.maybe(types.reference(DocumentModel)),
		logo: types.maybe(types.reference(DocumentModel)),
		//
		statistics: types.frozen<AccountStatistics>()
	})
	.views((self) => ({
		get marriageContacts() {
			return self.contacts.filter((c) => {
				return (
					c.contactRole?.id === ContactRole.SPOUSE ||
					c.contactRole?.id === ContactRole.CHILD ||
					c.contactRole?.id === ContactRole.PARENT ||
					c.contactRole?.id === ContactRole.SIBLING
				);
			});
		},
		get externalContacts() {
			return self.contacts.filter(
				(c) => c.contactRole?.id !== ContactRole.SPOUSE && c.contactRole?.id !== ContactRole.CHILD
			);
		}
	}))
	.views((self) => ({
		getNoChildrenUnder(age: number) {
			return self.contacts.filter((c) => c.contactRole?.id === ContactRole.CHILD).filter((c) => c.age < age)
				.length;
		}
	}))
	.actions((self) => ({
		resetStatistics() {
			self.statistics = {
			};
		}
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
		get familyContacts(): any[] {
			return self.contactsSnapshot.filter(
				(item) =>
					item.contactRole?.id === "spouse" ||
					item.contactRole?.id === "child" ||
					item.contactRole?.id === "parent" ||
					item.contactRole?.id === "sibling" ||
					item.contactRole?.id === "extended_family"
			);
		},
		get otherContacts(): any[] {
			return self.contactsSnapshot.filter(
				(item) =>
					item.contactRole?.id !== "spouse" &&
					item.contactRole?.id !== "child" &&
					item.contactRole?.id !== "parent" &&
					item.contactRole?.id !== "sibling" &&
					item.contactRole?.id !== "extended_family"
			);
		}
	}))
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
		get formSnapshot(): AccountSnapshot & {
			contactsInfo?: any[];
			familyContacts?: any[];
			otherContacts?: any[];
		} {
			return Object.assign({}, toJS(getSnapshot(self)), {
				contactsInfo: self.contactsSnapshot,
				familyContacts: self.familyContacts,
				otherContacts: self.otherContacts
			});
		}
	}));

type MstAccountType = typeof MstAccountModel;
export interface MstAccount extends MstAccountType { }
export const AccountModel: MstAccount = MstAccountModel;
export interface Account extends Instance<typeof AccountModel> { }
export type AccountSnapshot = SnapshotIn<typeof MstAccountModel>;
export type AccountPayload = Omit<AccountSnapshot, "id">;
