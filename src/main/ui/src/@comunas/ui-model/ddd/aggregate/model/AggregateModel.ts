import { Optional } from "@comunas/ui-model";
import { toJS, transaction } from "mobx";
import { applyPatch, getRoot, getSnapshot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeInfo, EntityTypes } from "../../../app/common/config/EntityTypes";
import { UserInfo } from "../../../app/session";
import { AggregateMeta } from "./AggregateMeta";
import { AggregateStore } from "./AggregateStore";

const MstAggregateModel = types
	.model("Aggregate", {
		id: types.identifier,
		caption: types.maybe(types.string),
		//
		meta: types.maybe(types.frozen<AggregateMeta>()),
		//
		owner: types.frozen<UserInfo>()
	})
	.views((self) => ({
		get isNew(): boolean {
			return self.id === "##NEW##";
		},
		get allowStore(): boolean {
			return true;
		}
	}))
	.views((self) => ({
		get rootStore(): AggregateStore {
			return getRoot(self);
		}
	}))
	.views((self) => ({
		// abstract properties, overwrite if necessary
		get type(): EntityTypeInfo {
			const itemType = self.meta?.itemType;
			return EntityTypes[itemType?.id.substring(4)!];
		},
		get isObj(): boolean {
			return false;
		},
		get isDoc(): boolean {
			return false;
		}
	}))
	.actions((self) => ({
		setOwner(owner: UserInfo) {
			self.owner = owner;
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
						path: "/" + field.split(".").join("/"),
						value: value === null ? undefined : value
					});
					self.calcAll();
				});
			} catch (error: any) {
				console.error(`AggregateModel.setField(${field}, ${value}) CRASHED:`, error);
			}
		}
	}))
	.views((self) => ({
		// must be last view, so type info is complete
		get apiSnapshot(): AggregateSnapshot {
			return toJS(getSnapshot(self));
		},
		get formSnapshot(): AggregateSnapshot {
			return toJS(getSnapshot(self));
		}
	}));

type MstAggregateType = typeof MstAggregateModel;
export interface MstAggregate extends MstAggregateType { }
export const AggregateModel: MstAggregate = MstAggregateModel;
export interface Aggregate extends Instance<typeof AggregateModel> { }
export type MstAggregateSnapshot = SnapshotIn<typeof AggregateModel>;
export interface AggregateSnapshot extends Optional<MstAggregateSnapshot, "caption"> { }
export type AggregatePayload = Omit<AggregateSnapshot, "id">;
