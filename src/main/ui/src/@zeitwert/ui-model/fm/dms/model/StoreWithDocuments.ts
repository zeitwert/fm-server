
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { DOCUMENT_API } from "../service/DocumentApi";
import { Document, DocumentModel, DocumentSnapshot } from "./DocumentModel";

const MstStoreWithDocumentsModel = StoreWithEntitiesModel.named("StoreWithDocuments")
	.props({
		documents: types.optional(types.map(DocumentModel), {})
	})
	.views((self) => ({
		getDocument(id: string): Document | undefined {
			return self.documents.get(id);
		},
	}))
	.actions((self) => ({
		afterLoad(repository: EntityTypeRepository) {
			self.updateFromRepository(repository["document"], self.documents);
		}
	}))
	.actions((self) => ({
		async loadDocument(id: string): Promise<Document> {
			const document = self.getDocument(id);
			if (document) {
				return document;
			}
			await self.loadEntity<Document, DocumentSnapshot>(id, DOCUMENT_API);
			return self.getDocument(id)!;
		}
	}));

type MstStoreWithDocumentsType = typeof MstStoreWithDocumentsModel;
export interface MstStoreWithDocuments extends MstStoreWithDocumentsType { }
export const StoreWithDocumentsModel: MstStoreWithDocuments = MstStoreWithDocumentsModel;
export interface StoreWithDocuments extends Instance<typeof StoreWithDocumentsModel> { }
export type MstStoreWithDocumentsSnapshot = SnapshotIn<typeof MstStoreWithDocumentsModel>;
export interface StoreWithDocumentsSnapshot extends MstStoreWithDocumentsSnapshot { }
