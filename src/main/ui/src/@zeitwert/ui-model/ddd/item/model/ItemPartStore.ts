
import Logger from "loglevel";
import { transaction } from "mobx";
import { applySnapshot, flow, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository, requireThis, UUID } from "../../../app/common";
import { Aggregate } from "../../aggregate/model/AggregateModel";
import { ItemPartApi } from "../service/ItemPartApi";
import { ItemPart, ItemPartPayload, ItemPartSnapshot, MstItemPart } from "./ItemPartModel";

const MstItemPartStoreModel = types
	.model("ItemPartStore", {
		inTrx: types.optional(types.boolean, false)
	})
	.volatile(() => ({
		initialState: {} as ItemPartSnapshot
	}))
	// must overwrite
	.views((self) => ({
		get model(): MstItemPart {
			requireThis(false, "model() is implemented");
			return undefined!;
		},
		get api(): ItemPartApi<ItemPartSnapshot> {
			requireThis(false, "api() is implemented");
			return undefined!;
		},
		get item(): ItemPart | undefined {
			requireThis(false, "item() is implemented");
			return undefined;
		},
		get items(): ItemPart[] {
			requireThis(false, "items() is implemented");
			return [];
		},
		get parentTypeName(): string {
			requireThis(false, "parentTypeName() is implemented");
			return "";
		}
	}))
	// must overwrite
	.actions(() => ({
		setItem(part: ItemPart | undefined) {
			requireThis(false, "setItem() is implemented");
		},
		setItems(parts: ItemPartSnapshot[]) {
			requireThis(false, "setItem() is implemented");
		},
		removeItem() {
			requireThis(false, "removeItem() is implemented");
		}
	}))
	// lifecycle, do not overwrite
	.views((self) => ({
		get typeName() {
			return self.api.getItemType();
		},
		get isNew() {
			return !self.item || self.item.isNew;
		},
		get isInTrx() {
			return self.inTrx;
		}
	}))
	.actions((self) => {
		// memory transaction management, do not overwrite
		return {
			startTrx() {
				requireThis(!self.isInTrx, "not in transaction");
				self.initialState = getSnapshot(self.item as any);
				self.inTrx = true;
			},
			commitTrx() {
				requireThis(self.isInTrx, "in transaction");
				self.inTrx = false;
			},
			rollbackTrx() {
				requireThis(self.isInTrx, "in transaction");
				applySnapshot(self.item as any, self.initialState);
				self.inTrx = false;
			}
		};
	})
	// overwrite if necessary
	.actions((self) => ({
		updateStore(id: string, repository: any) {
			self.setItem(self.model.create(repository[self.typeName][id]));
			//self.updateStaticData(repository);
		}
	}))
	.actions((self) => ({
		// lifecycle, overwrite
		async create(item: Aggregate, initValues?: ItemPartPayload) {
			const method = "load" + self.parentTypeName.charAt(0).toUpperCase() + self.parentTypeName.slice(1);
			await self[method](item.id);
			self.setItem(
				self.model.create(
					Object.assign({}, initValues, {
						id: "New:" + UUID(),
						[self.parentTypeName]: item.id
					})
				)
			);
			self.startTrx();
		},
		loadByItem(item: Aggregate) {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<ItemPart[], any[]>(function* (): any {
				try {
					const repository = yield self.api.loadParts(item.id, self.parentTypeName);
					if (repository[self.typeName]) {
						self.setItems(
							Object.keys(repository[self.typeName]).map((id) => repository[self.typeName][id])
						);
					}
					//self.updateStaticData(repository);
					return self.items;
				} catch (error: any) {
					Logger.error("Failed to load parts", error);
					return Promise.reject(error);
				}
			})();
		},
		load(item: Aggregate, id: string) {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<ItemPart, any[]>(function* (): any {
				try {
					const repository = yield self.api.loadPart(item.id, id);
					self.updateStore(id, repository);
					return self.item;
				} catch (error: any) {
					Logger.error("Failed to load part", error);
					return Promise.reject(error);
				}
			})();
		},
		edit() {
			self.startTrx();
		},
		cancel() {
			self.rollbackTrx();
		},
		store(item: Aggregate) {
			requireThis(self.isInTrx, "in transaction");
			return flow<ItemPart, any[]>(function* (): any {
				try {
					const snapshot = self.item!.apiSnapshot!;
					let repository: EntityTypeRepository;
					let id: string;
					if (self.isNew) {
						repository = yield self.api.addPart(snapshot, item.id);
						id = Object.keys(repository[self.typeName])[Object.keys(repository[self.typeName]).length - 1];
					} else {
						repository = yield self.api.storePart(snapshot, item.id);
						id = self.item!.id;
					}
					transaction(() => {
						self.updateStore(id, repository);
						self.commitTrx();
					});
					return self.item;
				} catch (error: any) {
					Logger.error("Failed to store part", error);
					self.rollbackTrx();
					return Promise.reject(error);
				}
			})();
		},
		remove(item: Aggregate) {
			return flow(function* () {
				try {
					yield self.api.removePart(self.item!.apiSnapshot, item.id);
					self.removeItem();
				} catch (error: any) {
					Logger.error("Failed to remove part", error);
				}
			})();
		}
	}));

type MstItemPartStoreType = typeof MstItemPartStoreModel;
export interface MstItemPartStore extends MstItemPartStoreType { }

export const ItemPartStoreModel: MstItemPartStore = MstItemPartStoreModel;
export type ItemPartStoreModelType = typeof ItemPartStoreModel;
export interface ItemPartStore extends Instance<ItemPartStoreModelType> { }
export type ItemPartStoreSnapshot = SnapshotIn<ItemPartStoreModelType>;
export type ItemPartStorePayload = Omit<ItemPartStoreSnapshot, "id">;
