
import { Avatar, Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { EntityType, EntityTypeInfo, EntityTypes, session, User, UserInfo, UserStoreModel } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import { ActivityPortlet } from "lib/activity/ActivityPortlet";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import UserPasswordForm from "./modals/UserPasswordForm";
import UserStaticDataForm from "./tabs/UserStaticDataForm";
import UserSummaryForm from "./tabs/UserSummaryForm";

enum LEFT_TABS {
	OVERVIEW = "static-data",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	SUMMARY = "summary",
	ACTIVITY = "activity",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class UserPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.USER];

	@observable userStore = UserStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable activeRightTabId = RIGHT_TABS.SUMMARY;
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
		await this.userStore.load(this.props.params.userId!);
		session.setHelpContext(`${EntityType.USER}-${this.activeLeftTabId}`);
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
		const allowEdit = (allowEditStaticData && [LEFT_TABS.OVERVIEW].indexOf(this.activeLeftTabId) >= 0);

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
									{this.activeLeftTabId === LEFT_TABS.OVERVIEW && <UserStaticDataForm store={this.userStore} />}
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
							<TabsPanel label={<span>Steckbrief{!this.hasAvatar && <abbr className="slds-required"> *</abbr>}</span>}>
								{
									this.activeRightTabId === RIGHT_TABS.SUMMARY &&
									<UserSummaryForm user={user} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITY &&
									<ActivityPortlet {...Object.assign({}, this.props, { item: user, onSave: () => null as unknown as Promise<any> })} />
								}
							</TabsPanel>
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

	private openEditor = () => {
		this.userStore.edit();
	};

	private cancelEditor = async () => {
		this.userStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.userStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
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
