import { AggregateSnapshot } from "../../aggregate/model/AggregateModel";
import { AggregateApi } from "../../aggregate/service/AggregateApi";
import { ItemApiImpl } from "./impl/ItemApiImpl";

export interface ItemApi extends AggregateApi<AggregateSnapshot> {

	// getCounters(item: Aggregate): Promise<ItemCounters>;

	// changeOwner(item: Aggregate, user: UserInfo): Promise<any>;

	// getRecentByUser(user: UserInfo): Promise<EntityRepository>;

	// getFrequentByUser(user: UserInfo): Promise<EntityRepository>;

}

export const ITEM_API: ItemApi = new ItemApiImpl();
