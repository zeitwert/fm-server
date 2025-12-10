
import { EntityTypeRepository } from "../../../app/common";
import { AggregateSnapshot } from "../model/AggregateModel";

export interface AggregateApi<S extends AggregateSnapshot> {

	getModule(): string;

	getItemPath(): string;

	getItemType(): string;

	getAggregates(parameters?: string): Promise<EntityTypeRepository>;

	loadAggregate(id: string, noIncludes?: boolean): Promise<EntityTypeRepository>;

	createAggregate(item: S): Promise<EntityTypeRepository>;

	storeAggregate(item: S): Promise<EntityTypeRepository>;

	deleteAggregate(id: string): Promise<void>;

}
