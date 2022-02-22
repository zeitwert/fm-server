import { FormWrapper } from "@comunas/ui-forms";
import { Document, DocumentContentModel, DocumentContentSnapshot, FORM_API } from "@comunas/ui-model";
import { registerMetadataConfig } from "@finadvise/forms";
import { AppCtx } from "App";
import { inject } from "mobx-react";
import React from "react";

interface DocumentsWidgetProps {
	documents: Document[];
	isReadOnly?: boolean;
	onSelect?: (content: DocumentContentSnapshot) => void;
	onChange?: (content: DocumentContentSnapshot[]) => Promise<void>;
}

@inject("appStore")
export default class DocumentsWidget extends React.Component<DocumentsWidgetProps> {
	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: DocumentsWidgetProps) {
		super(props);
		registerMetadataConfig(
			async () => {
				const response = await this.ctx.appStore.getMetadataFormDefinition();
				return Promise.resolve(
					response.map((datum: any) => ({
						key: datum.id,
						title: datum.name,
						formDefinitionKey: datum.formKey
					}))
				);
			},
			(id) => FORM_API.getMetadataDefinition(id)
		);
	}

	render() {
		const { documents, isReadOnly, onSelect, onChange } = this.props;
		// TODO: investigate why the filter is needed, has some to do with documents
		return (
			<FormWrapper
				formId="item/editDocuments"
				payload={{
					documents: documents.filter((doc) => !!doc).map((doc) => this.getDocumentContent(doc)),
					control: {
						enabled: !isReadOnly
					}
				}}
				onUploadSelect={(path, value) => {
					onSelect && onSelect(value as DocumentContentSnapshot);
					return !isReadOnly;
				}}
				onAfterChange={(path, value) =>
					path.includes("documents") && onChange && onChange(value as DocumentContentSnapshot[])
				}
				displayMode={isReadOnly ? "readonly" : "enabled"}
			/>
		);
	}

	private getDocumentContent(document: Document) {
		if (document.content && document.isInstance) {
			document.content.syncMetadata(document);
			return document.content.formSnapshot;
		}
		return DocumentContentModel.create({
			// @ts-ignore
			id: Number(document.id),
			name: document.name!,
			objId: Number(document.id),
			editable: false
		}).formSnapshot;
	}
}
