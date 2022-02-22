
import { toJS } from "mobx";
import { getRoot, getSnapshot, IAnyModelType, Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjModel } from "../../../ddd/obj/model/ObjModel";
import {
	DocumentContent,
	DocumentContentModel,
	DocumentContentSnapshot,
	MetadataFormDef
} from "./DocumentContentModel";
import { DocumentStore } from "./DocumentStore";

enum Type {
	Standalone = "standalone",
	Template = "template",
	Instance = "instance"
}

enum ContentType {
	Pdf = "pdf",
	Form = "form"
}

enum ClientVisibility {
	Public = "public",
	Private = "private"
}

export interface DocumentCategory extends Enumerated {
	documentLifecycle: Enumerated;
}

export interface DocumentStatus extends Enumerated {
	documentLifecycle: Enumerated;
}

export interface DocumentContentType extends Enumerated {
	mimeType: string;
	extension: string;
}

const MstDocumentModel = ObjModel.named("Document")
	.props({
		name: types.maybe(types.string),
		description: types.maybe(types.string),
		intlKey: types.maybe(types.string),
		metadataFormDef: types.maybe(types.frozen<MetadataFormDef>()),
		documentType: types.maybe(types.frozen<Enumerated>()),
		templateDocument: types.maybe(types.reference(types.late((): IAnyModelType => DocumentModel))),
		documentCategory: types.maybe(types.frozen<DocumentCategory>()),
		documentStatus: types.maybe(types.frozen<DocumentStatus>()),
		documentClientVisibility: types.maybe(types.frozen<Enumerated>()),
		// ContentType.document
		content: types.maybe(DocumentContentModel),
		contentType: types.maybe(types.frozen<DocumentContentType>()),
		// ContentType.url
		url: types.maybe(types.string),
		// ContentType.form
		formId: types.maybe(types.string),
		// Eligibility
		areas: types.optional(types.array(types.frozen<Enumerated>()), [])
	})
	.views((self) => ({
		get contentTypeId(): string | undefined {
			return self.contentType?.id;
		},
		get isUrl() {
			return !!self.url;
		},
		get isForm() {
			return self.contentType?.id === ContentType.Form;
		},
		get isPdf() {
			return self.contentType?.id === ContentType.Pdf;
		}
	}))
	.views((self) => ({
		get isDocument() {
			return !self.isForm;
		},
		get isStandalone() {
			return self.documentType?.id === Type.Standalone;
		},
		get isTemplate() {
			return self.documentType?.id === Type.Template;
		},
		get isInstance() {
			return self.documentType?.id === Type.Instance;
		},
		get isPublic() {
			return self.documentClientVisibility?.id === ClientVisibility.Public;
		},
		get isPrivate() {
			return self.documentClientVisibility?.id === ClientVisibility.Private;
		}
	}))
	.views((self) => ({
		get completeName() {
			return self.name + (self.isInstance && self.content ? " (" + self.content.name + ")" : "");
		}
	}))
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
	.views((self) => ({
		get apiSnapshot() {
			return Object.assign({}, toJS(getSnapshot(self)), {
				content: self.content?.apiSnapshot
			});
		}
	}));

type MstDocumentType = typeof MstDocumentModel;
export interface MstDocument extends MstDocumentType { }
export const DocumentModel: MstDocument = MstDocumentModel;
export interface Document extends Instance<typeof DocumentModel> { }
export type MstDocumentSnapshot = SnapshotIn<typeof MstDocumentModel>;
export interface DocumentSnapshot extends MstDocumentSnapshot { }
export type DocumentPayload = Omit<DocumentSnapshot, "id">;
