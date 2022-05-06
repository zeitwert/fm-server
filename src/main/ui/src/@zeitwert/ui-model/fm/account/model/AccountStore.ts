import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithContactsModel } from "../../contact/model/StoreWithContacts";
import { ACCOUNT_API } from "../service/AccountApi";
import { Account, AccountModel, AccountSnapshot } from "./AccountModel";

const MstAccountStoreModel = ObjStoreModel
	.named("AccountStore")
	.props({
		contactsStore: types.optional(StoreWithContactsModel, {}),
		account: types.maybe(AccountModel)
	})
	.views((self) => ({
		get model() {
			return AccountModel;
		},
		get api() {
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
			self.contactsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
	.actions((self) => ({
		setItem(snapshot: AccountSnapshot) {
			self.account = cast(snapshot);
		}
	}));

type MstAccountStoreType = typeof MstAccountStoreModel;
export interface MstAccountStore extends MstAccountStoreType { }
export const AccountStoreModel: MstAccountStore = MstAccountStoreModel;
export interface AccountStore extends Instance<typeof AccountStoreModel> { }
export type MstAccountStoreSnapshot = SnapshotIn<typeof MstAccountStoreModel>;
export interface AccountStoreSnapshot extends MstAccountStoreSnapshot { }
