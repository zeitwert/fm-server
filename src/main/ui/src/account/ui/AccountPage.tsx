
import { Avatar, Button, ButtonGroup, Icon, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account, AccountStoreModel, ContactStoreModel, EntityType, session } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import ItemEditor from "item/ui/ItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import ItemModal from "item/ui/ItemModal";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import AccountTabOrders from "./AccountTabOrders";
import ChangeOwnerButton from "./ChangeOwnerButton";
import AccountStaticDataForm from "./forms/AccountStaticDataForm";

enum TAB {
	DETAILS = 0,
	CASES = 1
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class AccountPage extends React.Component<RouteComponentProps> {

	@observable activeLeftTabId = TAB.DETAILS;
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
		if (session.isNetworkActive || !account) {
			return <Spinner variant="brand" size="large" />;
		}

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
							store={this.accountStore}
							entityType={EntityType.ACCOUNT}
							showEditButtons={this.activeLeftTabId === TAB.DETAILS}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
							key={"account-" + this.accountStore.account?.id}
						>
							<Tabs className="full-height" selectedIndex={this.activeLeftTabId} onSelect={(tabId: any) => (this.activeLeftTabId = tabId)} >
								<TabsPanel label="Details">
									{this.activeLeftTabId === TAB.DETAILS && <AccountStaticDataForm store={this.accountStore} />}
								</TabsPanel>
								<TabsPanel label={"Cases (" + this.accountStore.counters?.docCount + ")"}>
									{this.activeLeftTabId === TAB.CASES && (
										<AccountTabOrders
											account={this.accountStore.account!}
											template="doc.docs.by-account"
										/>
									)}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<ItemRightPart store={this.accountStore} />
				</ItemGrid>
				{
					this.contactStore.isInTrx &&
					<ItemModal
						entityType={EntityType.CONTACT}
						formId="contact/editContact"
						itemAlias={EntityType.CONTACT}
						store={this.contactStore}
						onClose={this.closeContactEditor}
						onCancel={this.cancelContactEditor}
					/>
				}
			</>
		);
	}

	private getHeaderDetails(account: Account): HeaderDetail[] {
		return [
			{ label: "Type", content: account.accountType?.name },
			{ label: "Client Segment", content: account.clientSegment?.name },
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
			{
				label: "Owner",
				content: account.owner!.caption,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={account.owner!.picture}
						imgAlt={account.owner!.caption}
						label={account.owner!.caption}
					/>
				),
				link: "/user/" + account.owner!.id
			},
		];
	}

	private getHeaderActions() {
		return (
			<>
				<ButtonGroup variant="list">
					<Button onClick={this.openContactEditor}>Add Contact</Button>
				</ButtonGroup>
				<ChangeOwnerButton accountStore={this.accountStore} />
			</>
		);
	}

	private openEditor = () => {
		this.accountStore.edit();
	};

	private cancelEditor = async () => {
		await this.accountStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.accountStore.store();
			this.ctx.showToast("success", `Account stored`);
			return item;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Account: " + (error.detail ? error.detail : error.title ? error.title : error)
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
			this.ctx.showToast("success", `New Contact ${this.contactStore.id} created`);
			this.props.navigate("/contact/" + this.contactStore.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not create new Contact: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(AccountPage);
