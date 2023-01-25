
import { Document } from "@zeitwert/ui-model";
import React from "react";

interface DocumentsWidgetProps {
	documents: Document[];
	isReadOnly?: boolean;
	onSelect?: (content: any /*DocumentContentSnapshot*/) => void;
	onChange?: (content: any /*DocumentContentSnapshot[]*/) => Promise<void>;
}

export default class DocumentsWidget extends React.Component<DocumentsWidgetProps> {

	// constructor(props: DocumentsWidgetProps) {
	// 	super(props);
	// 	registerMetadataConfig(
	// 		async () => {
	// 			const response = await this.ctx.appStore.getMetadataFormDefinition();
	// 			return Promise.resolve(
	// 				response.map((datum: any) => ({
	// 					key: datum.id,
	// 					title: datum.name,
	// 					formDefinitionKey: datum.formKey
	// 				}))
	// 			);
	// 		},
	// 		(id) => FORM_API.getMetadataDefinition(id)
	// 	);
	// }

	render() {
		//const { documents, isReadOnly, onSelect, onChange } = this.props;
		// TODO: investigate why the filter is needed, has some to do with documents
		return (<></>
			// <FormWrapper
			// 	formId="item/editDocuments"
			// 	payload={{
			// 		documents: documents.filter((doc) => !!doc).map((doc) => this.getDocumentContent(doc)),
			// 		control: {
			// 			enabled: !isReadOnly
			// 		}
			// 	}}
			// 	onUploadSelect={(path: any, value: any) => {
			// 		onSelect && onSelect(value as any /*DocumentContentSnapshot*/);
			// 		return !isReadOnly;
			// 	}}
			// 	onAfterChange={(path: any, value: any) =>
			// 		path.includes("documents") && onChange && onChange(value as any /*DocumentContentSnapshot[]*/)
			// 	}
			// 	displayMode={isReadOnly ? "readonly" : "enabled"}
			// />
		);
	}

	//	private getDocumentContent(document: Document) {
	// if (document.content && document.isInstance) {
	// 	document.content.syncMetadata(document);
	// 	return document.content.formSnapshot;
	// }
	// return DocumentContentModel.create({
	// 	id: Number(document.id),
	// 	name: document.name!,
	// 	objId: Number(document.id),
	// 	editable: false
	// }).formSnapshot;
	//	}

}
