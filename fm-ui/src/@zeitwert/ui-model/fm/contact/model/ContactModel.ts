import { toJS } from "mobx";
import { getRoot, getSnapshot, IAnyModelType, Instance, SnapshotIn, types } from "mobx-state-tree";
import { DateFormat, faTypes } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { AccountModel } from "../../account/model/AccountModel";
import { Address, AddressModel, AddressPayload, AddressSnapshot } from "./AddressModel";
import { ContactStore } from "./ContactStore";

const MstContactModel = ObjModel.named("Contact")
	.props({
		account: types.maybe(types.reference(types.late((): IAnyModelType => AccountModel))),
		//
		contactRole: types.maybe(types.frozen<Enumerated>()),
		salutation: types.maybe(types.frozen<Enumerated>()),
		title: types.maybe(types.frozen<Enumerated>()),
		firstName: types.maybe(types.string),
		lastName: types.maybe(types.string),
		description: types.maybe(types.string),
		birthDate: types.maybe(faTypes.date),
		//
		mobile: types.maybe(types.string),
		email: types.maybe(types.string),
		phone: types.maybe(types.string),
		//
		addresses: types.optional(types.array(AddressModel), []),
	})
	.views((self) => ({
		get accountSnapshot(): any {
			return self.account ? toJS(getSnapshot(self.account)) : undefined;
		}
	}))
	.views((self) => ({
		get fullName() {
			return self.firstName + " " + self.lastName;
		},
		get age() {
			if (!self.birthDate) {
				return 0;
			}
			return DateFormat.yearsDiff(new Date(self.birthDate), new Date());
		},
		get isMainContact() {
			return self.account?.mainContact?.id === self.id;
		}
	}))
	.views((self) => ({
		getAddresses(itemId: string | undefined): Address | undefined {
			return self.addresses.find((item) => item.id === itemId);
		},
		get postalAddresses() {
			return self.addresses.filter((item) => item.isPostalAddress === true);
		},
		get interactionChannels() {
			return self.addresses.filter((item) => item.isPostalAddress === false);
		},
	}))
	.actions((self) => {
		const superSetField = self.setField;
		async function setAccount(id: string) {
			id && (await (getRoot(self) as ContactStore).accountsStore.loadAccount(id));
			return superSetField("account", id);
		}
		async function setField(field: string, value: any) {
			if (field.startsWith("postalAddresses.")) {
				field = "addresses."
					.concat(
						self.addresses
							.findIndex((e) => e.id === self.postalAddresses[field.split(".")[1]].id)
							.toString()
					)
					.concat(".")
					.concat(field.split(".")[2]);
				return superSetField(field, value);
			} else if (field.startsWith("interactionChannels.")) {
				field = "addresses."
					.concat(
						self.addresses
							.findIndex((e) => e.id === self.interactionChannels[field.split(".")[1]].id)
							.toString()
					)
					.concat(".")
					.concat(field.split(".")[2]);
				return superSetField(field, value);
			}
			switch (field) {
				case "account": {
					return setAccount(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setAccount,
			setField
		};
	})
	.actions((self) => ({
		addAddress(address: AddressPayload, index?: number): Address {
			if (typeof index !== "undefined" && index >= 0) {
				self.addresses.splice(index, 0, address as AddressSnapshot);
			} else {
				self.addresses.push(AddressModel.create(address as AddressSnapshot));
				index = self.addresses.length - 1;
			}
			return self.addresses[index];
		},
		removeAddress(id: string) {
			self.addresses.splice(
				self.addresses.findIndex((a) => a.id === id),
				1
			);
		},
	}));

type MstContactType = typeof MstContactModel;
interface MstContact extends MstContactType { }

export const ContactModel: MstContact = MstContactModel;
export type ContactModelType = typeof ContactModel;
export interface Contact extends Instance<ContactModelType> { }
export type ContactSnapshot = SnapshotIn<ContactModelType>;
export type ContactPayload = Omit<ContactSnapshot, "id">;
