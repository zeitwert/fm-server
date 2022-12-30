import { toJS } from "mobx";
import { getSnapshot, IAnyModelType, Instance, SnapshotIn, types } from "mobx-state-tree";
import moment from "moment";
import { DateFormat, faTypes, requireThis, UUID } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import { AccountModel } from "../../account/model/AccountModel";
import { Address, AddressModel, AddressPayload, AddressSnapshot } from "./AddressModel";
import { Gender } from "./ContactEnums";
import { ContactStore } from "./ContactStore";
import { LifeEvent, LifeEventModel, LifeEventPayload } from "./LifeEventModel";

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
		lifeExpectancy: types.maybe(types.number),
		deathDate: types.maybe(faTypes.date),
		gender: types.maybe(types.frozen<Enumerated>()),
		civilStatus: types.maybe(types.frozen<Enumerated>()),
		nationality: types.maybe(types.frozen<Enumerated>()),
		domicileCountry: types.maybe(types.frozen<Enumerated>()),
		residenceCountry: types.maybe(types.frozen<Enumerated>()),
		passportNumber: types.maybe(types.string),
		socialSecurityNumber: types.maybe(types.string),
		disabilityDegree: types.maybe(types.number),
		occupation: types.maybe(types.string),
		occupationalStatus: types.maybe(types.frozen<Enumerated>()),
		workplace: types.maybe(types.string),
		isEmployer: types.maybe(types.boolean),
		phone: types.maybe(types.string),
		mobile: types.maybe(types.string),
		email: types.maybe(types.string),
		//
		addresses: types.optional(types.array(AddressModel), []),
		lifeEvents: types.optional(types.array(LifeEventModel), [])
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
		get isFemale() {
			return self.gender?.id === Gender.FEMALE;
		},
		get age() {
			if (!self.birthDate) {
				return 0;
			}
			return DateFormat.yearsDiff(new Date(self.birthDate), new Date());
		},
		get hasSomePhone() {
			return self.phone || self.mobile;
		},
		get isMainContact() {
			return self.account?.mainContact?.id === self.id;
		}
	}))
	.views((self) => ({
		get retirementAge() {
			if (self.isFemale) {
				return 64;
			}
			return 65;
		},
		get prematureDeathDate() {
			if (self.lifeExpectancy) {
				return moment(self.birthDate)
					.year(moment().year())
					.add(self.lifeExpectancy - self.age, "year")
					.toDate();
			}
			return undefined;
		}
	}))
	.views((self) => ({
		getAddresses(itemId: string | undefined): Address | undefined {
			return self.addresses.find((item) => item.id === itemId);
		},
		get postalAddresses() {
			const filterAddresses = self.addresses.filter((item) => item.isPostalAddress === true);
			return filterAddresses.slice().map((item) => item.formSnapshot);
		},
		get interactionChannels() {
			const filterAddresses = self.addresses.filter((item) => item.isPostalAddress === false);
			return filterAddresses.slice().map((item) => item.formSnapshot);
		},
		getLifeEvents(itemId: string | undefined): LifeEvent | undefined {
			return self.lifeEvents.find((item) => item.id === itemId);
		}
	}))
	.actions((self) => {
		const superSetField = self.setField;
		async function setAccount(id: string) {
			id && (await (self.rootStore as ContactStore).accountsStore.loadAccount(id));
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
			const snapshot = Object.assign({}, address, {
				id: "New:" + UUID()
			});
			if (typeof index !== "undefined" && index >= 0) {
				self.addresses.splice(index, 0, snapshot);
			} else {
				self.addresses.push(AddressModel.create(snapshot));
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
		setItem(part: LifeEvent | undefined) {
			requireThis(false, "setItem() is implemented");
		}
	}))
	.actions((self) => ({
		addLifeEvent(initValues?: LifeEventPayload) {
			const newLifeEvent = LifeEventModel.create(
				Object.assign({}, initValues, {
					id: "New:" + UUID(),
					lifeEventTypeId: "anniversary",
					lifeEventNotificationId: "all",
					isDeterministic: false,
					isOwn: true,
					isGoal: false
				})
			);
			self.lifeEvents.push(newLifeEvent);
			return newLifeEvent;
		},
		removeLifeEvent(id: string) {
			self.lifeEvents.splice(
				self.lifeEvents.findIndex((le) => le.id === id),
				1
			);
		},
		setLifeEventBirthDateName() {
			self.lifeEvents
				.filter((le) => le.isDeterministic)
				.filter((le) => le.name === "Birth Date")
				.forEach((le) => {
					le.name = self.firstName + "'s" + le.name;
				});
		}
	}))
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {
				addresses: self.addresses.map((a) => a.apiSnapshot),
				lifeEvents: self.lifeEvents.map((le) => le.apiSnapshot)
			});
		},
		get formSnapshot(): ContactSnapshot & {
			postalAddresses?: AddressSnapshot[];
			interactionChannels?: AddressSnapshot[];
			age?: number;
		} {
			return Object.assign({}, toJS(getSnapshot(self)), {
				postalAddresses: self.postalAddresses,
				interactionChannels: self.interactionChannels,
				age: self.age,
				prematureDeathDate: self.prematureDeathDate,
				isMainContact: self.isMainContact
			});
		}
	}));

type MstContactType = typeof MstContactModel;
export interface MstContact extends MstContactType { }

export const ContactModel: MstContact = MstContactModel;
export type ContactModelType = typeof ContactModel;
export interface Contact extends Instance<ContactModelType> { }
export type ContactSnapshot = SnapshotIn<ContactModelType>;
export type ContactPayload = Omit<ContactSnapshot, "id">;
