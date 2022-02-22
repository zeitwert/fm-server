
import { Account, AccountStoreModel, ContactStoreModel, EntityType } from "@comunas/ui-model";
import Avatar from "@salesforce/design-system-react/components/avatar";
import Button from "@salesforce/design-system-react/components/button";
import ButtonGroup from "@salesforce/design-system-react/components/button-group";
import Icon from "@salesforce/design-system-react/components/icon";
import Spinner from "@salesforce/design-system-react/components/spinner";
import Tabs from "@salesforce/design-system-react/components/tabs";
import TabsPanel from "@salesforce/design-system-react/components/tabs/panel";
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
	@observable isLoaded = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.accountStore.load(this.props.params.accountId!);
		this.isLoaded = true;
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.accountId !== prevProps.params.accountId) {
			await this.accountStore.load(this.props.params.accountId!);
			this.isLoaded = true;
		}
	}

	render() {
		if (!this.isLoaded) {
			return <Spinner variant="brand" size="large" />;
		}
		const account = this.accountStore.account!;
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
						>
							<Tabs className="full-height" selectedIndex={this.activeLeftTabId} onSelect={(tabId) => (this.activeLeftTabId = tabId)} >
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
					<ItemRightPart store={this.accountStore} account={account} />
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
			{
				label: "Main Contact",
				content: account.mainContact?.caption,
				icon: account.mainContact ? <Icon category="standard" name="contact" size="small" /> : undefined,
				link: account.mainContact ? "/contact/" + account.mainContact?.id : undefined
			},
			{ label: "Type", content: account.accountType?.name },
			{ label: "Client Segment", content: account.clientSegment?.name },
			{ label: "Main Phone", content: account.mainContact?.phone },
			{
				label: "Main Email",
				content: account.mainContact?.email,
				url: "mailto:" + account.mainContact?.email
			}
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
