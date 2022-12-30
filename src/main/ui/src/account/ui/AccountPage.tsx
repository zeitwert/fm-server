
import { Avatar, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account, AccountStoreModel, ContactStoreModel, EntityType, EntityTypeInfo, EntityTypes, session, UserInfo } from "@zeitwert/ui-model";
import { ActivityPortlet } from "activity/ActivityPortlet";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
import ItemEditor from "item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemPage";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import AccountStaticDataForm from "./forms/AccountStaticDataForm";
import AccountSummaryTab from "./tabs/AccountSummaryTab";

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
class AccountPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.ACCOUNT];

	@observable accountStore = AccountStoreModel.create({});
	@observable contactStore = ContactStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable activeRightTabId = RIGHT_TABS.SUMMARY;

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
		session.setHelpContext(`${EntityType.ACCOUNT}-${this.activeLeftTabId}`);

		const allowEditStaticData = session.isAdmin || session.hasSuperUserRole;
		const isActive = !account.meta?.closedAt;
		const allowEdit = (allowEditStaticData && [LEFT_TABS.OVERVIEW].indexOf(this.activeLeftTabId) >= 0);

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
									{this.activeLeftTabId === LEFT_TABS.OVERVIEW && <AccountStaticDataForm store={this.accountStore} />}
								</TabsPanel>
								{
									/*
								<TabsPanel label={"Cases (" + this.accountStore.counters?.docCount + ")"}>
									{this.activeLeftTabId === LEFT_TABS.CASES && (
										<AccountTabOrders
											account={this.accountStore.account!}
											template="doc.docs.by-account"
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
							<TabsPanel label={<span>Steckbrief{!this.hasLogo && <abbr className="slds-required"> *</abbr>}</span>}>
								{
									this.activeRightTabId === RIGHT_TABS.SUMMARY &&
									<AccountSummaryTab account={account} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITY &&
									<ActivityPortlet {...Object.assign({}, this.props, { item: account, onSave: () => null as unknown as Promise<any> })} />
								}
							</TabsPanel>
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					/*
					this.contactStore.isInTrx &&
					<ItemModal
						entityType={EntityType.CONTACT}
						formId="contact/editContact"
						itemAlias={EntityType.CONTACT}
						store={this.contactStore}
						onClose={this.closeContactEditor}
						onCancel={this.cancelContactEditor}
					/>
					*/
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
				{
					/*
					<ButtonGroup variant="list">
						<Button onClick={this.openContactEditor}>Add Contact</Button>
					</ButtonGroup>
					<ChangeOwnerButton accountStore={this.accountStore} />
					*/
				}
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
	/*
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
				this.ctx.showToast("success", `New Contact ${this.contactStore.id} created`);
				this.props.navigate("/contact/" + this.contactStore.id);
			} catch (error: any) {
				this.ctx.showAlert(
					"error",
					"Could not create new Contact: " + (error.detail ? error.detail : error.title ? error.title : error)
				);
			}
		};
	*/

	private reload = async () => {
		this.accountStore.load(this.accountStore.id!);
	};

}

export default withRouter(AccountPage);
