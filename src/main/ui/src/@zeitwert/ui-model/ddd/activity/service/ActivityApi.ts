import { EntityTypeRepository } from "../../../app/common";
import { UserInfo } from "../../../app/session";
import { Aggregate } from "../../aggregate/model/AggregateModel";
import { AggregateApi } from "../../aggregate/service/AggregateApi";
import { ActivitySnapshot } from "../model/ActivityModel";
import { ActivityApiImpl } from "./impl/ActivityApiImpl";

export interface ActivityApi extends AggregateApi<ActivitySnapshot> {

	findByUser(user: UserInfo): Promise<EntityTypeRepository>;

	findByItem(item: Aggregate): Promise<EntityTypeRepository>;

}

export const ACTIVITY_API: ActivityApi = new ActivityApiImpl();
