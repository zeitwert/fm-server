
import Logger from "loglevel";
import { transaction } from "mobx";
import { applySnapshot, cast, flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { API, Config } from "../../../app/common";
import { ObjStoreModel } from "../../../ddd/obj/model/ObjStore";
import { DocumentApi, DOCUMENT_API } from "../service/DocumentApi";
import { Document, DocumentContentType, DocumentModel, DocumentModelType, DocumentSnapshot } from "./DocumentModel";

const MstDocumentStoreModel = ObjStoreModel.named("DocumentStore")
	.props({
		document: types.maybe(DocumentModel),
		availableDocuments: types.optional(types.array(types.reference(DocumentModel)), [])
	})
	.views((self) => ({
		get model(): DocumentModelType {
			return DocumentModel;
		},
		get api(): DocumentApi {
			return DOCUMENT_API;
		},
		get item(): Document | undefined {
			return self.document;
		}
	}))
	.actions((self) => ({
		setItem(snapshot: DocumentSnapshot | undefined) {
			if (self.document && snapshot) {
				applySnapshot(self.document, snapshot);
			} else if (snapshot) {
				self.document = cast(snapshot);
			} else {
				self.document = undefined;
			}
		}
	}))
	.actions(() => ({
		getContentTypeByMimeType(mimeType: string) {
			return flow(function* () {
				const response = yield API.get(Config.getEnumUrl("base", "codeContentType?mimeType=" + mimeType));
				return response.data[0] as DocumentContentType;
			})();
		},
		getContentTypeByExtension(extension: string) {
			return flow(function* () {
				const response = yield API.get(Config.getEnumUrl("base", "codeContentType?extension=" + extension));
				return response.data[0] as DocumentContentType;
			})();
		}
	}))
	.actions((self) => ({
		getAvailableDocuments() {
			return flow<Document[], any[]>(function* (): any {
				try {
					const repository = yield DOCUMENT_API.getAvailableDocuments();
					transaction(() => {
						//self.updateStaticData(repository);
						Object.keys(repository.document).forEach((key) => self.availableDocuments.push(key));
					});
					return self.availableDocuments;
				} catch (error: any) {
					Logger.error("Failed to get available documents", error);
					return Promise.reject(error);
				}
			})();
		}
	}));

type MstDocumentStoreType = typeof MstDocumentStoreModel;
interface MstDocumentStore extends MstDocumentStoreType { }

export const DocumentStoreModel: MstDocumentStore = MstDocumentStoreModel;
export type DocumentStoreModelType = typeof DocumentStoreModel;
export interface DocumentStore extends Instance<DocumentStoreModelType> { }
export type DocumentStoreSnapshot = SnapshotIn<DocumentStoreModelType>;
export type DocumentStorePayload = Omit<DocumentStoreSnapshot, "id">;
