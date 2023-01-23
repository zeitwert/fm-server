
import { Avatar, Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { EntityType, EntityTypeInfo, EntityTypes, NotesStore, NotesStoreModel, session, User, UserInfo, UserStoreModel } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import NotesTab from "lib/item/ui/tab/NotesTab";
import ObjActivityHistoryTab from "lib/item/ui/tab/ObjActivityHistoryTab";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import UserPasswordForm from "./modals/UserPasswordForm";
import UserDocumentsTab from "./tabs/UserDocumentsTab";
import UserMainForm from "./tabs/UserMainForm";

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
class UserPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.USER];

	@observable userStore = UserStoreModel.create({});
	@observable notesStore: NotesStore = NotesStoreModel.create({});

	@observable activeLeftTabId = LEFT_TABS.MAIN;
	@observable activeRightTabId = RIGHT_TABS.DOCUMENTS;
	@observable doChangePassword = false;

	@computed
	get hasAvatar(): boolean {
		return !!this.userStore.user?.avatar?.contentTypeId;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		session.setHelpContext(`${EntityType.USER}-${this.activeLeftTabId}`);
		await this.userStore.load(this.props.params.userId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.userId !== prevProps.params.userId) {
			await this.userStore.load(this.props.params.userId!);
		}
	}

	render() {

		const user = this.userStore.user!;
		if (!user && session.isNetworkActive) {
			return <></>;
		} else if (!user) {
			return <NotFound entityType={this.entityType} id={this.props.params.userId!} />;
		}

		const allowEditStaticData = session.isAdmin;
		const isActive = !user.meta?.closedAt;
		const allowEdit = (allowEditStaticData && [LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.notesStore.notes.length;

		return (
			<>
				<ItemHeader
					store={this.userStore}
					details={this.getHeaderDetails(user)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart>
						<ItemEditor
							key={"user-" + this.userStore.user?.id}
							store={this.userStore}
							entityType={EntityType.USER}
							showEditButtons={isActive && allowEdit && !session.hasReadOnlyRole}
						>
							<Tabs
								className="full-height"
								selectedIndex={LEFT_TAB_VALUES.indexOf(this.activeLeftTabId)}
								onSelect={(tabId: number) => (this.activeLeftTabId = LEFT_TAB_VALUES[tabId])}
							>
								<TabsPanel label="Details">
									{this.activeLeftTabId === LEFT_TABS.MAIN && <UserMainForm user={this.userStore.user!} doEdit={this.userStore.isInTrx} />}
								</TabsPanel>
								{
									/*
								<TabsPanel label={"Cases (" + this.userStore.counters?.docCount + ")"}>
									{this.activeLeftTabId === LEFT_TABS.CASES && (
										<UserTabOrders
											user={this.userStore.user!}
											template="doc.docs.by-user"
										/>
									)}
								</TabsPanel>
									*/
								}
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<ItemRightPart isFullWidth={false}>
						<Tabs
							className="full-height"
							selectedIndex={RIGHT_TAB_VALUES.indexOf(this.activeRightTabId)}
							onSelect={(tabId: number) => (this.activeRightTabId = RIGHT_TAB_VALUES[tabId])}
						>
							<TabsPanel label={<span>Dokumente{!this.hasAvatar && <abbr className="slds-required"> *</abbr>}</span>}>
								{
									this.activeRightTabId === RIGHT_TABS.DOCUMENTS &&
									<UserDocumentsTab user={user} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label={"Notizen" + (notesCount ? ` (${notesCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab relatedToId={this.userStore.id!} notesStore={this.notesStore} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<ObjActivityHistoryTab obj={user} />
								}
							</TabsPanel>
							{
								user.hasValidations &&
								<TabsPanel label={`Validierungen (${user.validationsCount})`}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={user.meta?.validationList!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					this.doChangePassword && (
						<UserPasswordForm
							onCancel={this.cancelPasswordEditor}
							onClose={this.closePasswordEditor}
						/>
					)
				}
				{
					session.isNetworkActive &&
					<Spinner variant="brand" size="large" />
				}
			</>
		);
	}

	private getHeaderDetails(user: User): HeaderDetail[] {
		const owner: UserInfo = user.owner as UserInfo;
		return [
			{ label: "Email", content: user.email },
			{ label: "Name", content: user.name },
			{
				label: "Owner",
				content: owner.name,
				icon: (
					!!owner.id &&
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(owner.id)}
						imgAlt={owner.name}
						label={owner.name}
					/>
				),
				link: "/user/" + user.owner!.id
			},
			{ label: "Mandant", content: user.tenant?.name },
		];
	}

	private getHeaderActions() {
		return (
			<ButtonGroup variant="list">
				<Button onClick={this.openPasswordEditor}>Passwort ändern</Button>
			</ButtonGroup>
		);
	}

	private openPasswordEditor = () => {
		this.doChangePassword = true;
	};

	private cancelPasswordEditor = () => {
		this.doChangePassword = false;
	};

	private closePasswordEditor = async (password: string, requestChange: boolean) => {
		try {
			this.userStore.edit();
			const user = this.userStore.item as User;
			user.setField("password", password);
			user.setField("needPasswordChange", requestChange);
			const item = await this.userStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			this.doChangePassword = false;
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.userStore.load(this.props.params.userId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

	private reload = async () => {
		this.userStore.load(this.userStore.id!);
	};

}

export default withRouter(UserPage);
