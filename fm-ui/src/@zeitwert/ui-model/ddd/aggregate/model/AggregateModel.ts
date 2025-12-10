
import { transaction } from "mobx";
import { applyPatch, getRoot, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Optional } from "../../../../ui-model/app/common/utils/Optional";
import { EntityTypeInfo, EntityTypes } from "../../../app/common/config/EntityTypes";
import { UserInfo } from "../../../app/session";
import { AggregateMeta } from "./AggregateMeta";
import { AggregateStore } from "./AggregateStore";
import { Enumerated } from "./EnumeratedModel";

const NewId = "##NEW##";

const MstAggregateModel = types
	.model("Aggregate", {
		id: types.identifier,
		caption: types.maybe(types.string),
		//
		meta: types.maybe(types.frozen<AggregateMeta>()),
		//
		tenant: types.maybe(types.frozen<Enumerated>()),
		owner: types.maybe(types.frozen<Enumerated>())
	})
	.views((self) => ({
		get isNew(): boolean {
			return self.id === NewId;
		},
		get allowStore(): boolean {
			return true;
		},
		get hasValidations(): boolean {
			return (self.meta?.validations?.length ?? 0) > 0;
		},
		get hasErrors(): boolean {
			return self?.meta?.validations?.filter(v => v.validationLevel?.id === "error").length! > 0;
		},
		get validationsCount(): number {
			return self.meta?.validations?.length ?? 0;
		},
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
	.actions((self) => ({
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
	.actions((self) => ({
		async calcOnServer() {
			await (getRoot(self) as AggregateStore).calcOnServer();
		},
		async execOperation(operations: string[]) {
			await (getRoot(self) as AggregateStore).execOperation(operations);
		}
	}))
	// preProcessSnapshot before snapshot is applied to the model
	.preProcessSnapshot((snapshot) => {
		if (!snapshot) {
			return snapshot;
		}
		return Object.assign({}, snapshot, { id: snapshot.id ?? NewId });
	})
	// postProcessSnapshot after snapshot is retrieved from model
	.postProcessSnapshot((snapshot) => {
		if (!snapshot) {
			return snapshot;
		}
		return Object.assign({}, snapshot, { id: snapshot.id === NewId ? undefined : snapshot.id });
	});

type MstAggregateType = typeof MstAggregateModel;
interface MstAggregate extends MstAggregateType { }

export const AggregateModel: MstAggregate = MstAggregateModel;
export type AggregateModelType = typeof AggregateModel;
export interface Aggregate extends Instance<AggregateModelType> { }
export type AggregateSnapshot = Optional<SnapshotIn<AggregateModelType>, "caption">;
export type AggregatePayload = Omit<AggregateSnapshot, "id">;
