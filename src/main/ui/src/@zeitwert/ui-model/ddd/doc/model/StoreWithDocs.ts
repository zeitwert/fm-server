
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../aggregate/model/StoreWithEntities";
import { DOC_API } from "../service/DocApi";
import { Doc, DocModel, DocSnapshot } from "./DocModel";

const MstStoreWithDocsModel = StoreWithEntitiesModel.named("StoreWithDocs")
	.props({
		docs: types.optional(types.map(DocModel), {})
	})
	.views((self) => ({
		getDoc(id: string): Doc | undefined {
			return self.docs.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["doc"], self.docs);
		}
	}))
	.actions((self) => ({
		async loadDoc(id: string): Promise<Doc> {
			const doc = self.getDoc(id);
			if (doc) {
				return doc;
			}
			await self.loadEntity<Doc, DocSnapshot>(id, DOC_API);
			return self.getDoc(id)!;
		}
	}));

type MstStoreWithDocsType = typeof MstStoreWithDocsModel;
export interface MstStoreWithDocs extends MstStoreWithDocsType { }
export const StoreWithDocsModel: MstStoreWithDocs = MstStoreWithDocsModel;
export interface StoreWithDocs extends Instance<typeof StoreWithDocsModel> { }
export type MstStoreWithDocsSnapshot = SnapshotIn<typeof MstStoreWithDocsModel>;
export interface StoreWithDocsSnapshot extends MstStoreWithDocsSnapshot { }
