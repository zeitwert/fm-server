import { Button } from "@salesforce/design-system-react";
import { AggregateStore, Doc, DocStore, Document, Enumerated, Obj, ObjStore } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import AppGlobalModal from "app/ui/AppGlobalModal";
import DocumentsWidget from "areas/document/ui/DocumentsWidget";
import { DocumentViewer } from "areas/document/ui/DocumentViewer";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import DocumentEditor from "./DocumentEditor";

interface DocumentsTabProps {
	store: AggregateStore;
	areas?: Enumerated[];
}

@inject("showAlert", "showToast")
@observer
export default class DocumentsTab extends React.Component<DocumentsTabProps> {
	@observable selectedDocument?: Document;
	@observable isEditorOpen = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: DocumentsTabProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { store, areas } = this.props;
		const item = store.item! as Obj | Doc;
		return (
			<div className="slds-m-around_medium">
				<DocumentsWidget
					documents={[]/*item.documents*/}
					onSelect={(content) =>
						(this.selectedDocument = undefined /*item.documents.find((d) => d.id === content.objId?.toString())*/)
					}
					isReadOnly
				/>
				<Button
					label="Edit"
					onClick={() => {
						this.props.store.edit();
						this.isEditorOpen = true;
					}}
					variant="brand"
					className="slds-m-vertical_medium"
				/>
				{this.isEditorOpen && (
					<AppGlobalModal
						path={[
							{
								title: (item as any)?.name || item.caption,
								iconCategory: item.type.iconCategory,
								iconName: item.type.iconName
							},
							{
								title: "Document Editor"
							}
						]}
						onPrimaryAction={this.closeDocumentEditor}
						onSecondaryAction={this.cancelDocumentEditor}
						onClose={() => (this.isEditorOpen = false)}
					>
						<DocumentEditor
							store={store}
							documents={[]/*item.documents*/}
							areas={areas}
							onAdd={(document) => item.setDocuments([]/*item.documents.concat([document])*/)}
							onSet={(documents) => item.setDocuments(documents)}
						/>
					</AppGlobalModal>
				)}
				{this.selectedDocument && (
					<AppGlobalModal
						path={[
							{
								title: (item as any)?.name || item.caption,
								iconCategory: item.type.iconCategory,
								iconName: item.type.iconName
							},
							{
								title: "Document Viewer: " + this.selectedDocument!.completeName
							}
						]}
						onClose={() => (this.selectedDocument = undefined)}
					>
						<DocumentViewer document={this.selectedDocument} doEdit={false} />
					</AppGlobalModal>
				)}
			</div>
		);
	}

	private cancelDocumentEditor = async () => {
		await this.props.store.cancel();
	};

	private closeDocumentEditor = async () => {
		const store: ObjStore | DocStore = this.props.store as any;
		try {
			await store.updateDocuments();
			await store.store();
			this.ctx.showToast("success", `Documents stored`);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Documents: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};
}
