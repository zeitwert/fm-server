
import { toJS } from "mobx";
import { getSnapshot, IAnyModelType, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";

export enum DocumentCategory {
	Foto = "foto"
}

export enum DocumentKind {
	Standalone = "standalone",
	Template = "template",
	Instance = "instance"
}

export enum ContentKind {
	Document = "document",
	Foto = "foto",
	Video = "video"
}

// enum ContentType {
// 	Pdf = "pdf",
// 	Form = "form"
// }

export interface DocumentContentType extends Enumerated {
	mimeType: string;
	extension: string;
}

const MstDocumentModel = ObjModel.named("Document")
	.props({
		contentKind: types.maybe(types.frozen<Enumerated>()),
		supportedContentTypes: types.maybe(types.string),
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		documentKind: types.maybe(types.frozen<Enumerated>()),
		documentCategory: types.maybe(types.frozen<Enumerated>()),
		templateDocument: types.maybe(types.reference(types.late((): IAnyModelType => DocumentModel))),
		contentType: types.maybe(types.frozen<DocumentContentType>()),
	})
	.views((self) => ({
		get contentTypeId(): string | undefined {
			return self.contentType?.id;
		},
	}))
	.views((self) => ({
		get isStandalone() {
			return self.documentKind?.id === DocumentKind.Standalone;
		},
		get isTemplate() {
			return self.documentKind?.id === DocumentKind.Template;
		},
		get isInstance() {
			return self.documentKind?.id === DocumentKind.Instance;
		},
	}))
	.views((self) => ({
		get completeName() {
			return self.name;
		}
	}))
	/*
		.actions((self) => ({
			syncMetadata(content: DocumentContentSnapshot) {
				if (self.content) {
					self.content.name = content.name;
					self.content.versionNr = content.versionNr || null;
				}
				if (content.metadataProperties) {
					self.documentStatus = content.metadataProperties.documentStatus;
					self.documentClientVisibility = content.metadataProperties.documentClientVisibility;
					self.description = content.metadataProperties.description;
				}
			}
		}))
	*/
	/*
		.actions((self) => {
			const superSetField = self.setField;
			return {
				async setField(field: string, value: any) {
					const store: DocumentStore = getRoot(self);
					switch (field) {
						case "content": {
							const content = (value[0] || undefined) as DocumentContent;
							if (content && content.mimeType) {
								superSetField("contentType", await store.getContentTypeByMimeType(content.mimeType));
							}
							return superSetField("content", content);
						}
						case "url": {
							const parts = value?.split(".");
							if (parts && parts.length > 1) {
								superSetField(
									"contentType",
									await store.getContentTypeByExtension(parts[parts.length - 1])
								);
							}
							return superSetField(field, value);
						}
						default: {
							return superSetField(field, value);
						}
					}
				}
			};
		})
	*/
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {});
		}
	}));

type MstDocumentType = typeof MstDocumentModel;
interface MstDocument extends MstDocumentType { }

export const DocumentModel: MstDocument = MstDocumentModel;
export type DocumentModelType = typeof DocumentModel;
export interface Document extends Instance<DocumentModelType> { }
export type DocumentSnapshot = SnapshotIn<DocumentModelType>;
export type DocumentPayload = Omit<DocumentSnapshot, "id">;
