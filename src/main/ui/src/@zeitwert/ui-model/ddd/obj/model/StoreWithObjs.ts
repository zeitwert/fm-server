
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../aggregate/model/StoreWithEntities";
import { OBJ_API } from "../service/ObjApi";
import { Obj, ObjModel, ObjSnapshot } from "./ObjModel";

const MstStoreWithObjsModel = StoreWithEntitiesModel.named("StoreWithObjs")
	.props({
		objs: types.optional(types.map(ObjModel), {})
	})
	.views((self) => ({
		getObj(id: string): Obj | undefined {
			return self.objs.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["obj"], self.objs);
		}
	}))
	.actions((self) => ({
		async loadObj(id: string): Promise<Obj> {
			const obj = self.getObj(id);
			if (obj) {
				return obj;
			}
			await self.loadEntity<Obj, ObjSnapshot>(id, OBJ_API);
			return self.getObj(id)!;
		}
	}));

type MstStoreWithObjsType = typeof MstStoreWithObjsModel;
export interface MstStoreWithObjs extends MstStoreWithObjsType { }
export const StoreWithObjsModel: MstStoreWithObjs = MstStoreWithObjsModel;
export interface StoreWithObjs extends Instance<typeof StoreWithObjsModel> { }
export type MstStoreWithObjsSnapshot = SnapshotIn<typeof MstStoreWithObjsModel>;
export interface StoreWithObjsSnapshot extends MstStoreWithObjsSnapshot { }
