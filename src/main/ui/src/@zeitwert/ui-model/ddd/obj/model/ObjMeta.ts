import { UserInfo } from "../../../../ui-model/app";
import { AggregateMeta } from "../../aggregate/model/AggregateMeta";

export interface ObjMeta extends AggregateMeta {
	closedByUser?: UserInfo;
	closedAt?: Date;
}
