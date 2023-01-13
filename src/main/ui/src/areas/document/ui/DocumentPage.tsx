
import { Avatar, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Document, DocumentStoreModel, EntityType, EntityTypeInfo, EntityTypes, session, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart } from "lib/item/ui/ItemPage";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

enum LEFT_TABS {
	DETAILS = "static-data",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class DocumentPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.DOCUMENT];

	@observable documentStore = DocumentStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.DETAILS;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.documentStore.load(this.props.params.documentId!);
		session.setHelpContext(`${EntityType.DOCUMENT}-${this.activeLeftTabId}`);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.documentId !== prevProps.params.documentId) {
			await this.documentStore.load(this.props.params.documentId!);
		}
	}

	render() {

		const document = this.documentStore.document!;
		if (!document && session.isNetworkActive) {
			return <></>;
		} else if (!document) {
			return <NotFound entityType={this.entityType} id={this.props.params.documentId!} />;
		}

		const isActive = !document.meta?.closedAt;
		const allowEdit = ([LEFT_TABS.DETAILS].indexOf(this.activeLeftTabId) >= 0);

		return (
			<>
				<ItemHeader
					store={this.documentStore}
					details={this.getHeaderDetails(document)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart>
						<ItemEditor
							store={this.documentStore}
							entityType={EntityType.DOCUMENT}
							showEditButtons={isActive && allowEdit && !session.hasReadOnlyRole}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
						>
							<Tabs
								className="full-height"
								selectedIndex={LEFT_TAB_VALUES.indexOf(this.activeLeftTabId)}
								onSelect={(tabId: number) => (this.activeLeftTabId = LEFT_TAB_VALUES[tabId])}
							>
								<TabsPanel label="Details">
									<div>Well, that is inconvenient</div>
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
				</ItemGrid>
				{
					session.isNetworkActive &&
					<Spinner variant="brand" size="large" />
				}
			</>
		);
	}

	private getHeaderDetails(document: Document): HeaderDetail[] {
		const documentOwner: UserInfo = document.owner as UserInfo;
		return [
			{ label: "Document", content: document.contentKind?.name },
			{ label: "Content", content: document.contentType?.name },
			// {
			// 	label: "Areas",
			// 	content: document.areas?.map((mt) => mt.name).join(", ")
			// }
			{
				label: "Owner",
				content: documentOwner.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(documentOwner.id)}
						imgAlt={documentOwner.name}
						label={documentOwner.name}
					/>
				),
				link: "/user/" + documentOwner!.id
			},
		];
	}

	private getHeaderActions() {
		return (
			<>
				<ButtonGroup variant="list">
				</ButtonGroup>
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
