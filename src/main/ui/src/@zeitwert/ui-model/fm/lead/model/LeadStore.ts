import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { DocStoreModel } from "../../../ddd/doc/model/DocStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { StoreWithContactsModel } from "../../contact/model/StoreWithContacts";
import { LEAD_API } from "../service/LeadApi";
import { Lead, LeadModel, LeadSnapshot } from "./LeadModel";

const MstLeadStoreModel = DocStoreModel.named("LeadStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		contactsStore: types.optional(StoreWithContactsModel, {}),
		lead: types.maybe(LeadModel)
	})
	.views((self) => ({
		get model() {
			return LeadModel;
		},
		get api() {
			return LEAD_API;
		},
		get item(): Lead | undefined {
			return self.lead;
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.accountsStore.afterLoad(repository);
			self.contactsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(snapshot: LeadSnapshot) {
			self.lead = cast(snapshot);
		}
	}));

type MstLeadStoreType = typeof MstLeadStoreModel;
export interface MstLeadStore extends MstLeadStoreType { }

export const LeadStoreModel: MstLeadStore = MstLeadStoreModel;
export type LeadStoreModelType = typeof LeadStoreModel;
export interface LeadStore extends Instance<LeadStoreModelType> { }
export type LeadStoreSnapshot = SnapshotIn<LeadStoreModelType>;
export type LeadStorePayload = Omit<LeadStoreSnapshot, "id">;
