import { Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { DocumentStore, DocumentStoreModel, EntityType, session } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import FormItemEditor from "item/ui/FormItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

enum TAB {
	DETAILS = 0,
	ORDERS = 1
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class DocumentPage extends React.Component<RouteComponentProps> {

	@observable activeLeftTabId = TAB.DETAILS;
	@observable documentStore: DocumentStore = DocumentStoreModel.create({});

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.documentStore.load(this.props.params.documentId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.documentId !== prevProps.params.documentId) {
			await this.documentStore.load(this.props.params.documentId!);
		}
	}

	render() {
		const document = this.documentStore.document!;
		if (session.isNetworkActive || !document) {
			return <Spinner variant="brand" size="large" />;
		}
		const headerDetails: HeaderDetail[] = [
			{ label: "Document", content: document.contentKind?.name },
			{ label: "Content", content: document.contentType?.name },
			// {
			// 	label: "Areas",
			// 	content: document.areas?.map((mt) => mt.name).join(", ")
			// }
		];
		return (
			<>
				<ItemHeader store={this.documentStore} details={headerDetails} />
				<ItemGrid>
					<ItemLeftPart>
						<FormItemEditor
							store={this.documentStore}
							entityType={EntityType.DOCUMENT}
							formId="dms/editDocument"
							itemAlias={EntityType.DOCUMENT}
							showEditButtons={this.activeLeftTabId === TAB.DETAILS}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
							key={"document-" + this.documentStore.document?.id}
						>
							{(editor) => (
								<Tabs
									className="full-height"
									selectedIndex={this.activeLeftTabId}
									onSelect={(tabId: any) => (this.activeLeftTabId = tabId)}
								>
									<TabsPanel label="Details">
										{this.activeLeftTabId === TAB.DETAILS && editor}
									</TabsPanel>
									<TabsPanel label="Orders">
										{this.activeLeftTabId === TAB.ORDERS && (
											<p className="slds-p-around_medium">tbd</p>
										)}
									</TabsPanel>
								</Tabs>
							)}
						</FormItemEditor>
					</ItemLeftPart>
					<ItemRightPart store={this.documentStore} hideDocuments />
				</ItemGrid>
			</>
		);
	}

	private openEditor = () => {
		this.documentStore.edit();
	};

	private cancelEditor = async () => {
		await this.documentStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.documentStore.store();
			this.ctx.showToast("success", `Document stored`);
			return item;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Document: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(DocumentPage);
