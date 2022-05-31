
import { Aggregate } from "../..";
import { Enumerated } from "./EnumeratedModel";

export interface Validation {
	seqNr: number;
	validation: string;
	validationLevel: Enumerated;
}

export interface AggregateMeta {
	itemType: Enumerated;
	tenant: Aggregate;
	createdByUser: Aggregate;
	createdAt: Date;
	modifiedByUser: Aggregate;
	modifiedAt: Date;
	validationList: Validation[];
}
