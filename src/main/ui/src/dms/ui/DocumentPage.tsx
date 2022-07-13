import { Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { DocumentStore, DocumentStoreModel, EntityType, EntityTypeInfo, EntityTypes, session } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
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

	entityType: EntityTypeInfo = EntityTypes[EntityType.DOCUMENT];

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
		if (session.isNetworkActive) {
			return <Spinner variant="brand" size="large" />;
		} else if (!document) {
			return <NotFound entityType={this.entityType} id={this.props.params.documentId!} />;
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
		this.documentStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.documentStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.documentStore.load(this.props.params.documentId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

}

export default withRouter(DocumentPage);
