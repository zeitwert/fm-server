
import { transaction } from "mobx";
import { flow, IAnyType, IMSTMap, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityRepository, EntityTypeRepository } from "../../../app/common";
import { Aggregate, AggregateSnapshot } from "../../aggregate/model/AggregateModel";
import { AggregateApi } from "../../aggregate/service/AggregateApi";

const MstStoreWithEntitiesModel = types
	.model("StoreWithEntities", {
	})
	.actions((self) => ({
		updateFromRepository(repository: EntityRepository, entityMap: IMSTMap<IAnyType>) {
			if (repository) {
				transaction(() => {
					Object.keys(repository)
						.map((id) => repository[id])
						.forEach((snapshot) => entityMap.set(snapshot.id, snapshot));
				});
			}
		}
	}))
	// must overwrite
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
		}
	}))
	.actions((self) => ({
		loadEntity<T extends Aggregate, S extends AggregateSnapshot>(
			id: string,
			API: AggregateApi<S>
		) {
			return flow<T, any[]>(function* (): any {
				try {
					const repository = yield API.loadAggregate(id);
					self.afterLoad(repository);
				} catch (error: any) {
					console.error("Failed to load " + API.getItemType + ": " + id, error);
					return Promise.reject(error);
				}
			})();
		},
	}));

type MstStoreWithEntitiesType = typeof MstStoreWithEntitiesModel;
export interface MstStoreWithEntities extends MstStoreWithEntitiesType { }
export const StoreWithEntitiesModel: MstStoreWithEntities = MstStoreWithEntitiesModel;
export interface StoreWithEntities extends Instance<typeof StoreWithEntitiesModel> { }
export type MstStoreWithEntitiesSnapshot = SnapshotIn<typeof MstStoreWithEntitiesModel>;
export interface StoreWithEntitiesSnapshot extends MstStoreWithEntitiesSnapshot { }
