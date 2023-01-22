
import { Avatar, Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account, AccountStoreModel, ContactStoreModel, EntityType, EntityTypeInfo, EntityTypes, NotesStore, NotesStoreModel, session, TasksStore, TasksStoreModel, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import ContactCreationForm from "areas/contact/ui/ContactCreationForm";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import ItemModal from "lib/item/ui/ItemModal";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import NotesTab from "lib/item/ui/tab/NotesTab";
import ObjActivityHistoryTab from "lib/item/ui/tab/ObjActivityHistoryTab";
import TasksTab from "lib/item/ui/tab/TasksTab";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import AccountDocumentsTab from "./tabs/AccountDocumentsTab";
import AccountMainForm from "./tabs/AccountMainForm";

enum LEFT_TABS {
	MAIN = "main",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	DOCUMENTS = "documents",
	NOTES = "notes",
	TASKS = "tasks",
	ACTIVITIES = "activities",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class AccountPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.ACCOUNT];

	@observable accountStore = AccountStoreModel.create({});
	@observable contactStore = ContactStoreModel.create({});
	@observable notesStore: NotesStore = NotesStoreModel.create({});
	@observable tasksStore: TasksStore = TasksStoreModel.create({});

	@observable activeLeftTabId = LEFT_TABS.MAIN;
	@observable activeRightTabId = RIGHT_TABS.DOCUMENTS;

	@computed
	get hasLogo(): boolean {
		return !!this.accountStore.account?.logo?.contentTypeId;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		session.setHelpContext(`${EntityType.ACCOUNT}-${this.activeLeftTabId}`);
		await this.accountStore.load(this.props.params.accountId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.accountId !== prevProps.params.accountId) {
			await this.accountStore.load(this.props.params.accountId!);
		}
	}

	render() {

		const account = this.accountStore.account!;
		if (!account && session.isNetworkActive) {
			return <></>;
		} else if (!account) {
			return <NotFound entityType={this.entityType} id={this.props.params.accountId!} />;
		}

		const allowEditStaticData = session.isAdmin || session.hasSuperUserRole;
		const isActive = !account.meta?.closedAt;
		const allowEdit = (allowEditStaticData && [LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.notesStore.notes.length;
		const tasksCount = this.tasksStore.futureTasks.length + this.tasksStore.overdueTasks.length;

		return (
			<>
				<ItemHeader
					store={this.accountStore}
					details={this.getHeaderDetails(account)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart>
						<ItemEditor
							key={"account-" + this.accountStore.account?.id}
							store={this.accountStore}
							entityType={EntityType.ACCOUNT}
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
									{this.activeLeftTabId === LEFT_TABS.MAIN && <AccountMainForm account={this.accountStore.account!} doEdit={this.accountStore.isInTrx} />}
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
							<TabsPanel label={<span>Dokumente{!this.hasLogo && <abbr className="slds-required"> *</abbr>}</span>}>
								{
									this.activeRightTabId === RIGHT_TABS.DOCUMENTS &&
									<AccountDocumentsTab account={account} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label={"Notizen" + (notesCount ? ` (${notesCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab relatedToId={this.accountStore.id!} notesStore={this.notesStore} />
								}
							</TabsPanel>
							<TabsPanel label={"Aufgaben" + (tasksCount ? ` (${tasksCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.TASKS &&
									<TasksTab relatedToId={this.accountStore.id!} tasksStore={this.tasksStore} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<ObjActivityHistoryTab obj={account} />
								}
							</TabsPanel>
							{
								account.hasValidations &&
								<TabsPanel label={`Validierungen (${account.validationsCount})`}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={account.meta?.validationList!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					this.contactStore.isInTrx &&
					<ItemModal
						store={this.contactStore}
						entityType={EntityType.CONTACT}
						onClose={this.closeContactEditor}
						onCancel={this.cancelContactEditor}
					>
						{() => <ContactCreationForm contact={this.contactStore.contact!} />}
					</ItemModal>

				}
				{
					session.isNetworkActive &&
					<Spinner variant="brand" size="large" />
				}
			</>
		);
	}

	private getHeaderDetails(account: Account): HeaderDetail[] {
		const accountOwner: UserInfo = account.owner as UserInfo;
		return [
			{ label: "Type", content: account.accountType?.name },
			{ label: "Client Segment", content: account.clientSegment?.name },
			/*
			{
				label: "Main Contact",
				content: account.mainContact?.caption,
				icon: account.mainContact ? <Icon category="standard" name="contact" size="small" /> : undefined,
				link: account.mainContact ? "/contact/" + account.mainContact?.id : undefined
			},
			{
				label: "Email",
				content: account.mainContact?.email,
				url: "mailto:" + account.mainContact?.email
			},
			{ label: "Phone", content: account.mainContact?.phone },
			*/
			{
				label: "Owner",
				content: accountOwner.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(accountOwner.id)}
						imgAlt={accountOwner.name}
						label={accountOwner.name}
					/>
				),
				link: "/user/" + account.owner!.id
			},
			{ label: "Mandant", content: account.tenant?.name },
		];
	}

	private getHeaderActions() {
		return (
			<>
				<ButtonGroup variant="list">
					<Button onClick={this.openContactEditor}>Neuer Kontakt</Button>
				</ButtonGroup>
			</>
		);
	}

	private openEditor = () => {
		this.accountStore.edit();
	};

	private cancelEditor = async () => {
		this.accountStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.accountStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.accountStore.load(this.props.params.accountId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

	private openContactEditor = () => {
		this.contactStore.create({
			owner: this.ctx.session.sessionInfo!.user
		});
		this.contactStore.contact!.setAccount(this.accountStore.id!);
	};

	private cancelContactEditor = async () => {
		await this.contactStore.cancel();
	};

	private closeContactEditor = async () => {
		try {
			await this.contactStore.store();
			this.ctx.showToast("success", `Neuet Kontakt ${this.contactStore.id} eröffnet`);
			this.props.navigate("/contact/" + this.contactStore.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Konnte Kontakt nicht eröffnen: " + (error.detail ?? error.title ?? error)
			);
		}
	};

	private reload = async () => {
		this.accountStore.load(this.accountStore.id!);
	};

}

export default withRouter(AccountPage);
