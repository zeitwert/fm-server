
import { transaction } from "mobx";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { PortfolioApi, PORTFOLIO_API } from "../service/PortfolioApi";
import { Portfolio, PortfolioModel, PortfolioModelType, PortfolioSnapshot } from "./PortfolioModel";

const MstPortfolioStoreModel = ObjStoreModel.named("PortfolioStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
		portfolio: types.maybe(PortfolioModel)
	})
	.views((self) => ({
		get model(): PortfolioModelType {
			return PortfolioModel;
		},
		get api(): PortfolioApi {
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
		setItem(snapshot: PortfolioSnapshot) {
			transaction(() => {
				self.portfolio = cast({ id: snapshot.id } as PortfolioSnapshot);
				self.portfolio = cast(snapshot);
			});
		}
	}));

type MstPortfolioStoreType = typeof MstPortfolioStoreModel;
interface MstPortfolioStore extends MstPortfolioStoreType { }

export const PortfolioStoreModel: MstPortfolioStore = MstPortfolioStoreModel;
export type PortfolioStoreModelType = typeof PortfolioStoreModel;
export interface PortfolioStore extends Instance<PortfolioStoreModelType> { }
export type PortfolioStoreSnapshot = SnapshotIn<PortfolioStoreModelType>;
export type PortfolioStorePayload = Omit<PortfolioStoreSnapshot, "id">;
