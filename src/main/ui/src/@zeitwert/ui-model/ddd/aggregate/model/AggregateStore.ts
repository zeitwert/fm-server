import Logger from "loglevel";
import { transaction } from "mobx";
import {
	applySnapshot,
	flow,
	getSnapshot,
	Instance,
	IPatchRecorder,
	recordPatches,
	SnapshotIn,
	types
} from "mobx-state-tree";
import { EntityTypeRepository, requireThis } from "../../../app/common";
import { AggregateApi } from "../service/AggregateApi";
import { Aggregate, AggregatePayload, AggregateSnapshot, MstAggregate } from "./AggregateModel";

export interface AggregateCounters {
	docCount: number;
	documentCount: number;
	noteCount: number;
	stageHistoryCount: number;
}

const MstAggregateStoreModel = types
	.model("AggregateStore", {
		id: types.maybe(types.string),
		inTrx: types.optional(types.boolean, false),
		counters: types.maybe(types.frozen<AggregateCounters>())
	})
	.volatile(() => ({
		initialState: {} as AggregateSnapshot,
		recorder: undefined as IPatchRecorder | undefined
	}))
	// must overwrite
	.views((self) => ({
		get model(): MstAggregate {
			requireThis(false, "model() is implemented");
			return undefined!;
		},
		get api(): AggregateApi<AggregateSnapshot> {
			requireThis(false, "api() is implemented");
			return undefined!;
		},
		get item(): Aggregate | undefined {
			requireThis(false, "item() is implemented");
			return undefined;
		}
	}))
	// must overwrite
	.actions(() => ({
		setItem(aggregate: Aggregate) {
			requireThis(false, "setItem() is implemented");
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
	// memory transaction management, do not overwrite
	.actions((self) => ({
		startTrx() {
			requireThis(!self.isInTrx, "not in transaction");
			self.initialState = getSnapshot(self.item as any);
			self.recorder = recordPatches(self.item as any);
			self.inTrx = true;
		},
		commitTrx() {
			requireThis(self.isInTrx, "in transaction");
			self.recorder!.stop();
			self.inTrx = false;
		},
		rollbackTrx() {
			requireThis(self.isInTrx, "in transaction");
			applySnapshot(self.item as any, self.initialState);
			self.recorder!.stop();
			self.inTrx = false;
		}
	}))
	.views((self) => ({
		get changes() {
			const snapshot = self.item!.apiSnapshot;
			const result = {};
			self.recorder!.patches.forEach((patch) => {
				const prop = patch.path.split("/")[1];
				if (!result[prop]) {
					result[prop] = snapshot[prop];
				}
			});
			return result as AggregateSnapshot;
		}
	}))
	// overwrite if necessary
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
		}
	}))
	// lifecycle, do not overwrite
	.actions((self) => ({
		updateStore(id: string, repository: EntityTypeRepository) {
			transaction(() => {
				self.id = id;
				self.afterLoad(repository);
				self.setItem(self.model.create(repository[self.typeName][id]));
			});
		}
	}))
	.actions((self) => ({
		create(initValues?: AggregatePayload) {
			transaction(() => {
				self.id = undefined;
				self.setItem(self.model.create(Object.assign({}, initValues, { id: "##NEW##" })));
				self.startTrx();
			});
		},
		load(id: string) {
			requireThis(!self.isInTrx, "not in transaction");
			return flow<Aggregate, any[]>(function* (): any {
				try {
					const repository = yield self.api.loadAggregate(id);
					self.updateStore(id, repository);
					return self.item;
				} catch (error: any) {
					Logger.error("Failed to load item", error);
					return Promise.reject(error);
				}
			})();
		},
		edit() {
			self.startTrx();
		},
		async cancel() {
			self.rollbackTrx();
			return self.item!;
		},
		store() {
			requireThis(self.isInTrx, "in transaction");
			return flow<Aggregate, any[]>(function* (): any {
				try {
					let repository: EntityTypeRepository;
					let id: string;
					if (self.isNew) {
						repository = yield self.api.createAggregate(self.item!.apiSnapshot);
						id = Object.keys(repository[self.typeName])[Object.keys(repository[self.typeName]).length - 1];
					} else {
						repository = yield self.api.storeAggregate(
							Object.assign(self.changes, {
								id: self.item!.id
							})
						);
						id = self.item!.id;
					}
					transaction(() => {
						self.updateStore(id, repository);
						self.commitTrx();
					});
					return self.item;
				} catch (error: any) {
					Logger.error("Failed to store item", error);
					self.rollbackTrx();
					return Promise.reject(error);
				}
			})();
		}
		// }))
		// .actions((self) => ({
		// 	loadCounters() {
		// 		requireThis(!!self.item, "has item");
		// 		return flow(function* () {
		// 			try {
		// 				self.counters = yield ITEM_API.getCounters(self.item!);
		// 			} catch (error: any) {
		// 				Logger.error("Failed to load item counters", error);
		// 			}
		// 		})();
		// 	},
		// 	updateDocCount(count: number) {
		// 		self.counters = Object.assign({}, self.counters, {
		// 			docCount: count
		// 		});
		// 	},
		// 	updateDocumentCount(count: number) {
		// 		self.counters = Object.assign({}, self.counters, {
		// 			documentCount: count
		// 		});
		// 	},
		// 	updateNoteCount(count: number) {
		// 		self.counters = Object.assign({}, self.counters, {
		// 			noteCount: count
		// 		});
		// 	},
		// 	updateStageHistoryCount(count: number) {
		// 		self.counters = Object.assign({}, self.counters, {
		// 			stageHistoryCount: count
		// 		});
		// 	}
		// }))
		// .actions((self) => ({
		// 	changeOwner(user: UserInfo) {
		// 		requireThis(!!self.item, "has item");
		// 		return flow(function* () {
		// 			try {
		// 				yield ITEM_API.changeOwner(self.item!, user);
		// 				self.item!.setOwner(user);
		// 			} catch (error: any) {
		// 				Logger.error("Failed to change owner", error);
		// 			}
		// 		})();
		// 	}
	}));

type MstAggregateStoreType = typeof MstAggregateStoreModel;
export interface MstAggregateStore extends MstAggregateStoreType { }
export const AggregateStoreModel: MstAggregateStore = MstAggregateStoreModel;
export interface AggregateStore extends Instance<typeof AggregateStoreModel> { }
export type MstAggregateStoreSnapshot = SnapshotIn<typeof MstAggregateStoreModel>;
export interface AggregateStoreSnapshot extends MstAggregateStoreSnapshot { }
