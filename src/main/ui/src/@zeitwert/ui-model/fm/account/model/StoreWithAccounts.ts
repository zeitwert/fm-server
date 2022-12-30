
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
interface MstStoreWithAccounts extends MstStoreWithAccountsType { }

export const StoreWithAccountsModel: MstStoreWithAccounts = MstStoreWithAccountsModel;
export type StoreWithAccountsModelType = typeof StoreWithAccountsModel;
export interface StoreWithAccounts extends Instance<StoreWithAccountsModelType> { }
export type StoreWithAccountsSnapshot = SnapshotIn<StoreWithAccountsModelType>;
export type StoreWithAccountsPayload = Omit<StoreWithAccountsSnapshot, "id">;
