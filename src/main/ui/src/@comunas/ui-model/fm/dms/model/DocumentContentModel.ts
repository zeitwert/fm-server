import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { ObjPartModel } from "../../../ddd/obj/model/ObjPartModel";
import { Document, DocumentContentType, DocumentStatus } from "./DocumentModel";

export interface MetadataFormDef extends Enumerated {
	formKey: string;
}

const DocumentContentMetadata = types.model({
	type: types.maybe(types.string),
	documentStatus: types.maybe(types.frozen<DocumentStatus>()),
	documentClientVisibility: types.maybe(types.frozen<Enumerated>()),
	description: types.maybe(types.string)
});

const MstDocumentContentModel = ObjPartModel.named("DocumentContent")
	.props({
		id: types.maybe(types.number),
		name: types.string,
		size: types.optional(types.number, 0),
		versionNr: types.maybeNull(types.number),
		mimeType: types.optional(types.string, ""),
		contentType: types.maybe(types.frozen<DocumentContentType>()),
		downloadUrl: types.maybe(types.string),
		//objId: types.maybe(types.reference(types.late((): IAnyModelType => DocumentModel))),
		objId: types.maybeNull(types.number), // circular reference to DocumentModel
		// Computed / temporal values from forms.
		editable: types.optional(types.boolean, true),
		status: types.maybe(types.number),
		progress: types.maybe(types.number),
		fileData: types.maybe(types.frozen<File>()),
		metadataProperties: types.maybe(DocumentContentMetadata)
	})
	.actions((self) => ({
		syncMetadata(document: Document) {
			self.metadataProperties = {
				type: document.metadataFormDef?.id,
				documentStatus: document.documentStatus,
				documentClientVisibility: document.documentClientVisibility,
				description: document.description
			};
		}
	}));

type MstDocumentContentType = typeof MstDocumentContentModel;
export interface MstDocumentContent extends MstDocumentContentType { }
export const DocumentContentModel: MstDocumentContent = MstDocumentContentModel;
export interface DocumentContent extends Instance<typeof DocumentContentModel> { }
export type MstDocumentContentSnapshot = SnapshotIn<typeof DocumentContentModel>;
export interface DocumentContentSnapshot extends MstDocumentContentSnapshot { }
export type DocumentContentPayload = Omit<DocumentContentSnapshot, "id">;
