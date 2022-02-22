import { EntityTypeRepository } from "../../../app/common";
import { ItemPartSnapshot } from "../model/ItemPartModel";

export interface ItemPartApi<S extends ItemPartSnapshot> {

	getModule(): string;

	getItemPath(): string;

	getItemType(): string;

	loadParts(parentId: string, parentType: string): Promise<EntityTypeRepository>;

	loadPart(parentId: string, id: string): Promise<EntityTypeRepository>;

	addPart(part: S, parentId: string): Promise<EntityTypeRepository>;

	storePart(part: S, parentId: string): Promise<EntityTypeRepository>;

	removePart(part: S, parentId: string): Promise<void>;

}
