
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { CONTACT_API } from "../service/ContactApi";
import { Contact, ContactModel, ContactSnapshot } from "./ContactModel";

const MstStoreWithContactsModel = StoreWithEntitiesModel.named("StoreWithContacts")
	.props({
		contacts: types.optional(types.map(types.late(() => ContactModel)), {})
	})
	.views((self) => ({
		getContact(id: string): Contact | undefined {
			return self.contacts.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["contact"], self.contacts);
		}
	}))
	.actions((self) => ({
		async loadContact(id: string): Promise<Contact> {
			const contact = self.getContact(id);
			if (contact) {
				return contact;
			}
			await self.loadEntity<Contact, ContactSnapshot>(id, CONTACT_API);
			return self.getContact(id)!;
		}
	}));

type MstStoreWithContactsType = typeof MstStoreWithContactsModel;
interface MstStoreWithContacts extends MstStoreWithContactsType { }

export const StoreWithContactsModel: MstStoreWithContacts = MstStoreWithContactsModel;
export type StoreWithContactsModelType = typeof StoreWithContactsModel;
export interface StoreWithContacts extends Instance<StoreWithContactsModelType> { }
export type StoreWithContactsSnapshot = SnapshotIn<StoreWithContactsModelType>;
export type StoreWithContactsPayload = Omit<StoreWithContactsSnapshot, "id">;
