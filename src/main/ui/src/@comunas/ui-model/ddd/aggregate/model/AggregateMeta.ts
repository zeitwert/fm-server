
import { Aggregate } from "../..";
import { Enumerated } from "./EnumeratedModel";

export interface AggregateMeta {
	itemType: Enumerated;
	tenant: Aggregate;
	createdByUser: Aggregate;
	createdAt: Date;
	modifiedByUser: Aggregate;
	modifiedAt: Date;
}
