
import { toJS, transaction } from "mobx";
import { applyPatch, getRoot, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { AggregateStore } from "../../aggregate/model/AggregateStore";

const MstPartModel = types
	.model("Part", {
		id: types.identifier
	})
	.views((self) => ({
		get isNew() {
			return self.id.startsWith("New:");
		}
	}))
	.views((self) => ({
		get rootStore(): AggregateStore {
			return getRoot(self);
		}
	}))
	.actions(() => ({
		// calculation, overwrite if necessary
		async calcAll() { }
	}))
	.actions((self) => ({
		// state, overwrite if necessary for specific fields (f.ex. to load StaticDataStore)
		async setField(field: string, value: any) {
			try {
				transaction(() => {
					applyPatch(self, {
						op: self[field] ? "replace" : "add",
						path: "/" + field.replace(".", "/"),
						value: value === null ? undefined : value
					});
					self.calcAll();
				});
			} catch (error: any) {
				console.error(`PartModel.setField(${field}, ${value}) CRASHED:`, error);
			}
		}
	}))
	.views((self) => ({
		// must be last view, so type info is complete
		get apiSnapshot() {
			return toJS(getSnapshot(self));
		},
		get formSnapshot() {
			return toJS(getSnapshot(self));
		}
	}));

type MstPartType = typeof MstPartModel;
export interface MstPart extends MstPartType { }

export const PartModel: MstPart = MstPartModel;
export type PartModelType = typeof PartModel;
export interface Part extends Instance<PartModelType> { }
export type PartSnapshot = SnapshotIn<PartModelType>;
export type PartPayload = Omit<PartSnapshot, "id">;
