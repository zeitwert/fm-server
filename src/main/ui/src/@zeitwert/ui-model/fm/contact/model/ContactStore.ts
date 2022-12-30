import Logger from "loglevel";
import { cast, flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository, requireThis } from "../../../app/common";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { ContactApi, CONTACT_API } from "../service/ContactApi";
import { Contact, ContactModel, ContactModelType, ContactSnapshot } from "./ContactModel";
import { LifeEvent } from "./LifeEventModel";

const MstContactStoreModel = ObjStoreModel.named("ContactStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		contact: types.maybe(ContactModel)
	})
	.views((self) => ({
		get model(): ContactModelType {
			return ContactModel;
		},
		get api(): ContactApi {
			return CONTACT_API;
		},
		get item(): Contact | undefined {
			return self.contact;
		}
	}))
	.actions((self) => ({
		setItem(snapshot: ContactSnapshot) {
			self.contact = cast(snapshot);
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.accountsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		loadByEmail(email: string): Promise<Contact> {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<Contact, any[]>(function* (): any {
				try {
					const repository = yield CONTACT_API.getByEmail(email);
					if (!repository.contact) {
						return undefined;
					}
					const id = Object.keys(repository.contact)[Object.keys(repository.contact).length - 1];
					self.updateStore(id, repository);
					return self.contact;
				} catch (error: any) {
					Logger.error("Failed to get contact by email", error);
					return Promise.reject(error);
				}
			})();
		},
		loadAllLifeEvents(): Promise<LifeEvent[]> {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<LifeEvent[], any[]>(function* (): any {
				try {
					return yield CONTACT_API.getAllLifeEvents();
				} catch (error: any) {
					Logger.error("Failed to get life events", error);
					return Promise.reject(error);
				}
			})();
		}
	}));

type MstContactStoreType = typeof MstContactStoreModel;
interface MstContactStore extends MstContactStoreType { }

export const ContactStoreModel: MstContactStore = MstContactStoreModel;
export type ContactStoreModelType = typeof ContactStoreModel;
export interface ContactStore extends Instance<ContactStoreModelType> { }
export type ContactStoreSnapshot = SnapshotIn<ContactStoreModelType>;
export type ContactStorePayload = Omit<ContactStoreSnapshot, "id">;
