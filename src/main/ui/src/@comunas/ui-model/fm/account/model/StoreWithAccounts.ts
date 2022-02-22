
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { ACCOUNT_API } from "../service/AccountApi";
import { Account, AccountModel, AccountSnapshot } from "./AccountModel";

const MstStoreWithAccountsModel = StoreWithEntitiesModel.named("StoreWithAccounts")
	.props({
		accounts: types.optional(types.map(types.late(() => AccountModel)), {})
	})
	.views((self) => ({
		getAccount(id: string): Account | undefined {
			return self.accounts.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["account"], self.accounts);
		}
	}))
	.actions((self) => ({
		async loadAccount(id: string): Promise<Account> {
			const account = self.getAccount(id);
			if (account) {
				return account;
			}
			await self.loadEntity<Account, AccountSnapshot>(id, ACCOUNT_API);
			return self.getAccount(id)!;
		}
	}));

type MstStoreWithAccountsType = typeof MstStoreWithAccountsModel;
export interface MstStoreWithAccounts extends MstStoreWithAccountsType { }
export const StoreWithAccountsModel: MstStoreWithAccounts = MstStoreWithAccountsModel;
export interface StoreWithAccounts extends Instance<typeof StoreWithAccountsModel> { }
export type MstStoreWithAccountsSnapshot = SnapshotIn<typeof MstStoreWithAccountsModel>;
export interface StoreWithAccountsSnapshot extends MstStoreWithAccountsSnapshot { }
