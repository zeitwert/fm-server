import { Account } from "../../../fm/account/model/AccountModel";
import { AggregateApi } from "../../aggregate/service/AggregateApi";
import { Obj } from "../../obj/model/ObjModel";
import { Doc, DocSnapshot } from "../model/DocModel";
import { DocApiImpl } from "./impl/DocApiImpl";

export interface DocApi extends AggregateApi<DocSnapshot> {
	findByRefObj(refObj: Obj, params?: any): Promise<DocSnapshot[]>;

	findByRefDoc(refDoc: Doc, params?: any): Promise<DocSnapshot[]>;

	findByAccount(account: Account): Promise<DocSnapshot[]>;

	findUpcomingTasks(account: Account): Promise<DocSnapshot[]>;
}

export const DOC_API: DocApi = new DocApiImpl();
