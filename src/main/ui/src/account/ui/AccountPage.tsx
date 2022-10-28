
import { Avatar, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account, AccountStoreModel, ContactStoreModel, EntityType, EntityTypeInfo, EntityTypes, session, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
import ItemEditor from "item/ui/ItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import AccountStaticDataForm from "./forms/AccountStaticDataForm";

enum LEFT_TABS {
	OVERVIEW = "static-data",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class AccountPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.ACCOUNT];

	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable accountStore = AccountStoreModel.create({});
	@observable contactStore = ContactStoreModel.create({});

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
		if (session.isNetworkActive) {
			return <Spinner variant="brand" size="large" />;
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
					<ItemRightPart store={this.accountStore} />
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
						imgSrc={accountOwner.picture}
						imgAlt={accountOwner.caption}
						label={accountOwner.caption}
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
}

export default withRouter(AccountPage);
