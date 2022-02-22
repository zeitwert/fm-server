import { Account, DocumentStore, DocumentStoreModel, EntityType } from "@comunas/ui-model";
import Spinner from "@salesforce/design-system-react/components/spinner";
import Tabs from "@salesforce/design-system-react/components/tabs";
import TabsPanel from "@salesforce/design-system-react/components/tabs/panel";
import { AppCtx } from "App";
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
	@observable isLoaded = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.documentStore.load(this.props.params.documentId!);
		this.isLoaded = true;
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.documentId !== prevProps.params.documentId) {
			await this.documentStore.load(this.props.params.documentId!);
			this.isLoaded = true;
		}
	}

	render() {
		if (!this.isLoaded) {
			return <Spinner variant="brand" size="large" />;
		}
		const document = this.documentStore.document!;
		const headerDetails: HeaderDetail[] = [
			{ label: "Content", content: document.contentType?.name },
			{
				label: "Areas",
				content: document.areas?.map((mt) => mt.name).join(", ")
			}
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
						>
							{(editor) => (
								<Tabs
									className="full-height"
									selectedIndex={this.activeLeftTabId}
									onSelect={(tabId) => (this.activeLeftTabId = tabId)}
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
					<ItemRightPart
						store={this.documentStore}
						account={undefined as unknown as Account}
						hideDocuments
					/>
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
