import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Aggregate, AggregateModel } from "../../aggregate/model/AggregateModel";
import { PartModel } from "../../part/model/PartModel";

/**
 * This item part by default is a standalone part, so:
 *  - New parts have the item id assigned as the id of the parent (so only one new part can be created at a time)
 *  - We can do operations for the parts independently of the item, using their store
 */
const MstItemPartModel = PartModel.named("ItemPart")
	.props({
		item: types.maybe(types.reference(AggregateModel))
	})
	.views((self) => ({
		get parent(): Aggregate | undefined {
			return self.item;
		},
		get allowStore(): boolean {
			return true;
		}
	}));

type MstItemPartType = typeof MstItemPartModel;
export interface MstItemPart extends MstItemPartType { }

export const ItemPartModel: MstItemPart = MstItemPartModel;
export type ItemPartModelType = typeof ItemPartModel;
export interface ItemPart extends Instance<ItemPartModelType> { }
export type ItemPartSnapshot = SnapshotIn<ItemPartModelType>;
export type ItemPartPayload = Omit<ItemPartSnapshot, "id">;
