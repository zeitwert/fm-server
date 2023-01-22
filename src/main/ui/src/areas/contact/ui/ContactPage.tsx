import { Avatar, ButtonGroup, Icon, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Contact, ContactStoreModel, EntityType, EntityTypeInfo, EntityTypes, NotesStore, NotesStoreModel, session, TasksStore, TasksStoreModel, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import NotesTab from "lib/item/ui/tab/NotesTab";
import TasksTab from "lib/item/ui/tab/TasksTab";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import ContactMainForm from "./tabs/ContactMainForm";

enum LEFT_TABS {
	MAIN = "main",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	NOTES = "notes",
	TASKS = "tasks",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class ContactPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.CONTACT];

	@observable contactStore = ContactStoreModel.create({});
	@observable notesStore: NotesStore = NotesStoreModel.create({});
	@observable tasksStore: TasksStore = TasksStoreModel.create({});

	@observable activeLeftTabId = LEFT_TABS.MAIN;
	@observable activeRightTabId = RIGHT_TABS.NOTES;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		session.setHelpContext(`${EntityType.CONTACT}-${this.activeLeftTabId}`);
		await this.contactStore.load(this.props.params.contactId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.contactId !== prevProps.params.contactId) {
			await this.contactStore.load(this.props.params.contactId!);
		}
	}

	render() {

		const contact = this.contactStore.contact!;
		if (!contact && session.isNetworkActive) {
			return <></>;
		} else if (!contact) {
			return <NotFound entityType={this.entityType} id={this.props.params.contactId!} />;
		}

		const isActive = !contact.meta?.closedAt;
		const allowEdit = ([LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.notesStore.notes.length;
		const tasksCount = this.tasksStore.futureTasks.length + this.tasksStore.overdueTasks.length;

		return (
			<>
				<ItemHeader
					store={this.contactStore}
					details={this.getHeaderDetails(contact)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart>
						<ItemEditor
							store={this.contactStore}
							entityType={EntityType.CONTACT}
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
									{this.activeLeftTabId === LEFT_TABS.MAIN && <ContactMainForm contact={this.contactStore.contact!} doEdit={this.contactStore.isInTrx} />}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<ItemRightPart isFullWidth={false}>
						<Tabs
							className="full-height"
							selectedIndex={RIGHT_TAB_VALUES.indexOf(this.activeRightTabId)}
							onSelect={(tabId: number) => (this.activeRightTabId = RIGHT_TAB_VALUES[tabId])}
						>
							<TabsPanel label={"Notizen" + (notesCount ? ` (${notesCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab relatedToId={this.contactStore.id!} notesStore={this.notesStore} />
								}
							</TabsPanel>
							<TabsPanel label={"Aufgaben" + (tasksCount ? ` (${tasksCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.TASKS &&
									<TasksTab relatedToId={this.contactStore.id!} tasksStore={this.tasksStore} />
								}
							</TabsPanel>
							{
								contact.hasValidations &&
								<TabsPanel label={`Validierungen (${contact.validationsCount})`}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={contact.meta?.validationList!} />
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

	private getHeaderDetails(contact: Contact): HeaderDetail[] {
		const contactOwner: UserInfo = contact.owner as UserInfo;
		return [
			{
				label: "Account",
				content: contact.account?.caption,
				icon: <Icon category="standard" name="account" size="small" />,
				link: "/account/" + contact.account?.id
			},
			{
				label: "Email",
				content: contact.email,
				link: "mailto:" + contact.email
			},
			{
				label: "Mobile",
				content: contact.mobile,
				link: "tel:" + contact.mobile
			},
			{
				label: "Owner",
				content: contactOwner.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(contactOwner.id)}
						imgAlt={contactOwner.name}
						label={contactOwner.name}
					/>
				),
				link: "/user/" + contact.owner!.id
			},
		];
	}

	private getHeaderActions() {
		return (
			<ButtonGroup variant="list">
			</ButtonGroup>
		);
	}

	private openEditor = () => {
		this.contactStore.edit();
	};

	private cancelEditor = async () => {
		this.contactStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.contactStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.contactStore.load(this.props.params.contactId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

}

export default withRouter(ContactPage);
