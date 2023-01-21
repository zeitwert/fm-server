
import { UserInfo } from "../../../../ui-model/app";
import { AggregateMeta } from "../../aggregate/model/AggregateMeta";
import { Enumerated } from "../../aggregate/model/EnumeratedModel";

export interface ObjMeta extends AggregateMeta {
	closedByUser?: UserInfo;
	closedAt?: Date;
	transitions: ObjPartTransition[];
}

export interface ObjPartTransition {
	seqNr: number;
	user: Enumerated;
	timestamp: Date;
	changes: string;
}
