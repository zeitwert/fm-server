import { transaction } from "mobx";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithContactsModel } from "../../contact/model/StoreWithContacts";
import { StoreWithDocumentsModel } from "../../dms/model/StoreWithDocuments";
import { StoreWithTenantsModel } from "../../tenant/model/StoreWithTenants";
import { AccountApi, ACCOUNT_API } from "../service/AccountApi";
import { Account, AccountModel, AccountModelType, AccountSnapshot } from "./AccountModel";

const MstAccountStoreModel = ObjStoreModel
	.named("AccountStore")
	.props({
		tenantsStore: types.optional(StoreWithTenantsModel, {}),
		contactsStore: types.optional(StoreWithContactsModel, {}),
		documentsStore: types.optional(StoreWithDocumentsModel, {}),
		account: types.maybe(AccountModel)
	})
	.views((self) => ({
		get model(): AccountModelType {
			return AccountModel;
		},
		get api(): AccountApi {
			return ACCOUNT_API;
		},
		get item(): Account | undefined {
			return self.account;
		}
	}))
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.tenantsStore.afterLoad(repository);
			self.contactsStore.afterLoad(repository);
			self.documentsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(snapshot: AccountSnapshot | undefined) {
			transaction(() => {
				self.account = undefined;
				!!snapshot && (self.account = cast(snapshot));
			});
		}
	}));

type MstAccountStoreType = typeof MstAccountStoreModel;
interface MstAccountStore extends MstAccountStoreType { }

export const AccountStoreModel: MstAccountStore = MstAccountStoreModel;
export type AccountStoreModelType = typeof AccountStoreModel;
export interface AccountStore extends Instance<AccountStoreModelType> { }
export type AccountStoreSnapshot = SnapshotIn<AccountStoreModelType>;
export type AccountStorePayload = Omit<AccountStoreSnapshot, "id">;
