import { Account } from "../../../fm/account/model/AccountModel";
import { AggregateApi } from "../../aggregate/service/AggregateApi";
import { DocSnapshot } from "../model/DocModel";
import { DocApiImpl } from "./impl/DocApiImpl";

export interface DocApi extends AggregateApi<DocSnapshot> {

	findByAccount(account: Account): Promise<DocSnapshot[]>;

	findUpcomingTasks(account: Account): Promise<DocSnapshot[]>;

}

export const DOC_API: DocApi = new DocApiImpl();
