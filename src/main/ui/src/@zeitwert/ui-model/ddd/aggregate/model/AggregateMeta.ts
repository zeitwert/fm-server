
import { UserInfo } from "@zeitwert/ui-model/app";
import { Enumerated } from "./EnumeratedModel";

export interface Validation {
	seqNr: number;
	validation: string;
	validationLevel: Enumerated;
}

export interface AggregateMeta {
	itemType: Enumerated;
	tenant: Enumerated;
	version: number;
	createdByUser: UserInfo;
	createdAt: Date;
	modifiedByUser: UserInfo;
	modifiedAt: Date;
	validationList: Validation[];
	operationList: string[];
}
