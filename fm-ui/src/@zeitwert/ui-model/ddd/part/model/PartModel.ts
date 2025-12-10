
import { UUID } from "@zeitwert/ui-model/app";
import { transaction } from "mobx";
import { applyPatch, getRoot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { AggregateStore } from "../../aggregate/model/AggregateStore";

const NewIdPrefix = "New:";

const MstPartModel = types
	.model("Part", {
		id: types.identifier
	})
	.views((self) => ({
		get isNew() {
			return self.id.startsWith(NewIdPrefix);
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
	.preProcessSnapshot((snapshot) => {
		if (!snapshot) {
			return snapshot;
		}
		return Object.assign({}, snapshot, { id: snapshot.id ?? NewIdPrefix + UUID() });
	})
	.postProcessSnapshot((snapshot) => {
		if (!snapshot) {
			return snapshot;
		}
		return Object.assign({}, snapshot, { id: snapshot.id.startsWith(NewIdPrefix) ? undefined : snapshot.id });
	});

type MstPartType = typeof MstPartModel;
interface MstPart extends MstPartType { }

export const PartModel: MstPart = MstPartModel;
export type PartModelType = typeof PartModel;
export interface Part extends Instance<PartModelType> { }
export type PartSnapshot = SnapshotIn<PartModelType>;
export type PartPayload = Omit<PartSnapshot, "id">;
