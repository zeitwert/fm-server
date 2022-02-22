
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { PORTFOLIO_API } from "../service/PortfolioApi";
import { Portfolio, PortfolioModel } from "./PortfolioModel";

const MstPortfolioStoreModel = ObjStoreModel.named("PortfolioStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		portfolio: types.maybe(PortfolioModel)
	})
	.views((self) => ({
		get model() {
			return PortfolioModel;
		},
		get api() {
			return PORTFOLIO_API;
		},
		get item(): Portfolio | undefined {
			return self.portfolio;
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
		setItem(item: Portfolio) {
			self.portfolio = item;
		}
	}));

type MstPortfolioStoreType = typeof MstPortfolioStoreModel;
export interface MstPortfolioStore extends MstPortfolioStoreType { }
export const PortfolioStoreModel: MstPortfolioStore = MstPortfolioStoreModel;
export interface PortfolioStore extends Instance<typeof PortfolioStoreModel> { }
export type MstPortfolioStoreSnapshot = SnapshotIn<typeof MstPortfolioStoreModel>;
export interface PortfolioStoreSnapshot extends MstPortfolioStoreSnapshot { }
