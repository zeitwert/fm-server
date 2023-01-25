
import { Avatar, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Document, DocumentStoreModel, EntityType, EntityTypeInfo, EntityTypes, NotesStore, NotesStoreModel, session, TasksStore, TasksStoreModel, UserInfo } from "@zeitwert/ui-model";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import NotesTab from "lib/item/ui/tab/NotesTab";
import ObjActivityHistoryTab from "lib/item/ui/tab/ObjActivityHistoryTab";
import TasksTab from "lib/item/ui/tab/TasksTab";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

enum LEFT_TABS {
	MAIN = "main",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	NOTES = "notes",
	TASKS = "tasks",
	ACTIVITIES = "activities",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@observer
class DocumentPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.DOCUMENT];

	@observable documentStore = DocumentStoreModel.create({});
	@observable notesStore: NotesStore = NotesStoreModel.create({});
	@observable tasksStore: TasksStore = TasksStoreModel.create({});

	@observable activeLeftTabId = LEFT_TABS.MAIN;
	@observable activeRightTabId = RIGHT_TABS.NOTES;

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		session.setHelpContext(`${EntityType.DOCUMENT}-${this.activeLeftTabId}`);
		await this.documentStore.load(this.props.params.documentId!);
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
		const allowEdit = ([LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.notesStore.notes.length;
		const tasksCount = this.tasksStore.futureTasks.length + this.tasksStore.overdueTasks.length;

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
					<ItemRightPart>
						<Tabs
							className="full-height"
							selectedIndex={RIGHT_TAB_VALUES.indexOf(this.activeRightTabId)}
							onSelect={(tabId: number) => (this.activeRightTabId = RIGHT_TAB_VALUES[tabId])}
						>
							<TabsPanel label={"Notizen" + (notesCount ? ` (${notesCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab relatedToId={this.documentStore.id!} notesStore={this.notesStore} />
								}
							</TabsPanel>
							<TabsPanel label={"Aufgaben" + (tasksCount ? ` (${tasksCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.TASKS &&
									<TasksTab relatedToId={this.documentStore.id!} tasksStore={this.tasksStore} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<ObjActivityHistoryTab obj={document} />
								}
							</TabsPanel>
							{
								document.hasValidations &&
								<TabsPanel label={`Validierungen (${document.validationsCount})`}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validations={document.meta?.validations!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
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

}

export default withRouter(DocumentPage);
