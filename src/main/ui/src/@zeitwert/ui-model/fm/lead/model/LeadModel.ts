import { toJS } from "mobx";
import { getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { UserInfo } from "../../../app/session";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { DocModel } from "../../../ddd/doc/model/DocModel";
import { ContactModel, ContactSnapshot } from "../../contact/model/ContactModel";
import { LeadStore } from "./LeadStore";

export interface ConversionInfo {
	leadId: string;
	owner: UserInfo;
	account: ConversionAccountInfo;
	contact: ConversionContactInfo;
	docInfo: ConversionDocInfo;
}

export interface ConversionAccountInfo {
	doCreate: boolean;
	caption?: string; // doCreate
	info?: any; // !doCreate
}

export interface ConversionContactInfo {
	doCreate: boolean;
	salutation?: any; // doCreate
	firstName?: string; // doCreate
	lastName?: string; // doCreate
	info?: any; // !doCreate
}

export interface ConversionDocInfo {
	doCreate: boolean;
	docType?: any; // doCreate
	caption?: string; // doCreate
}

const MstLeadModel = DocModel.named("Lead")
	.props({
		areas: types.optional(types.array(types.frozen<Enumerated>()), []),
		//
		leadSource: types.maybe(types.frozen<Enumerated>()),
		//
		subject: types.maybe(types.string),
		description: types.maybe(types.string),
		//
		contact: types.maybe(types.reference(ContactModel)),
		//
		salutation: types.maybe(types.frozen<Enumerated>()),
		title: types.maybe(types.frozen<Enumerated>()),
		firstName: types.maybe(types.string),
		lastName: types.maybe(types.string),
		phone: types.maybe(types.string),
		mobile: types.maybe(types.string),
		email: types.maybe(types.string),
		leadRating: types.maybe(types.frozen<Enumerated>()),
		//
		street: types.maybe(types.string),
		zip: types.maybe(types.string),
		city: types.maybe(types.string),
		state: types.maybe(types.string),
		country: types.maybe(types.frozen<Enumerated>())
	})
	.views((self) => ({
		get contactSnapshot() {
			return self.contact ? (toJS(getSnapshot(self.contact)) as unknown as ContactSnapshot) : undefined;
		},
		get fullName() {
			return !!self.contact?.id
				? `${self.contact?.firstName} ${self.contact?.lastName}`
				: !!self.account?.id
					? `${self.account?.caption}`
					: `${self.firstName} ${self.lastName}`;
		},
		get hasSomePhone() {
			return self.phone || self.mobile;
		}
	}))
	.actions((self) => ({
		async calcAll() {
			self.caption = `${self.salutation?.name} ${self.firstName} ${self.lastName}`;
		}
	}))
	.actions((self) => {
		const superSetField = self.setField;
		async function setContact(id: string) {
			id && (await (self.rootStore as LeadStore).contactsStore.loadContact(id));
			superSetField("contact", id);
		}
		async function setField(field: string, value: any) {
			switch (field) {
				case "contact": {
					return setContact(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setContact,
			setField
		};
	});

type MstLeadType = typeof MstLeadModel;
export interface MstLead extends MstLeadType { }
export const LeadModel: MstLead = MstLeadModel;
export interface Lead extends Instance<typeof LeadModel> { }
export type MstLeadSnapshot = SnapshotIn<typeof MstLeadModel>;
export interface LeadSnapshot extends MstLeadSnapshot { }
export type LeadPayload = Omit<LeadSnapshot, "id">;
