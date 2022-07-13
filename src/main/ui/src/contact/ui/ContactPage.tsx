import { Avatar, ButtonGroup, Icon, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import {
	Contact,
	ContactStore,
	ContactStoreModel,
	DATE_FORMAT,
	EntityType,
	EntityTypeInfo,
	EntityTypes,
	session
} from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
import FormItemEditor from "item/ui/FormItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import moment from "moment";
import React from "react";
import ContactTabAddresses from "./ContactTabAddresses";
import ContactTabChannels from "./ContactTabChannels";

enum TAB {
	DETAILS = 0,
	CHANNELS = 1,
	ADDRESSES = 2
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class ContactPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.CONTACT];

	@observable activeLeftTabId = TAB.DETAILS;
	@observable contactStore: ContactStore = ContactStoreModel.create({});

	@observable updateCount = 0;
	@observable doEditContact = false;
	@observable isChannelsModalOpen = false;
	@observable isAddressesModalOpen = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.contactStore.load(this.props.params.contactId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.contactId !== prevProps.params.contactId) {
			await this.contactStore.load(this.props.params.contactId!);
		}
	}

	render() {
		const contact = this.contactStore.contact!;
		if (session.isNetworkActive) {
			return <Spinner variant="brand" size="large" />;
		} else if (!contact) {
			return <NotFound entityType={this.entityType} id={this.props.params.contactId!} />;
		}
		return (
			<>
				<ItemHeader
					store={this.contactStore}
					details={this.getHeaderDetails(contact)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart>
						<FormItemEditor
							store={this.contactStore}
							entityType={EntityType.DOCUMENT}
							formId="contact/editContact"
							itemAlias={EntityType.CONTACT}
							control={{
								birthDateWithAge: contact.age
									? contact.age + " years old, " + moment(contact.birthDate).format(DATE_FORMAT)
									: ""
							}}
							showEditButtons={
								this.activeLeftTabId === TAB.DETAILS ||
								this.activeLeftTabId === TAB.CHANNELS ||
								this.activeLeftTabId === TAB.ADDRESSES
							}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
							key={"contact-" + this.contactStore.contact?.id}
						>
							{(editor) => (
								<Tabs
									className="full-height"
									selectedIndex={this.activeLeftTabId}
									onSelect={(tabId: any) => (this.activeLeftTabId = tabId)}
								>
									<TabsPanel label="Details">
										{this.activeLeftTabId === TAB.DETAILS && editor}
									</TabsPanel>
									<TabsPanel
										label={
											"Channels (" +
											this.contactStore.contact?.interactionChannels.length +
											")"
										}
									>
										{this.activeLeftTabId === TAB.CHANNELS && (
											<ContactTabChannels
												key={this.updateCount}
												store={this.contactStore}
												displayMode={this.isChannelsModalOpen}
											/>
										)}
									</TabsPanel>
									<TabsPanel
										label={
											"Addresses (" + this.contactStore.contact?.postalAddresses.length + ")"
										}
									>
										{this.activeLeftTabId === TAB.ADDRESSES && (
											<ContactTabAddresses
												key={this.updateCount}
												store={this.contactStore}
												displayMode={this.isAddressesModalOpen}
											/>
										)}
									</TabsPanel>
								</Tabs>
							)}
						</FormItemEditor>
					</ItemLeftPart>
					<ItemRightPart store={this.contactStore} />
				</ItemGrid>
			</>
		);
	}

	private getHeaderDetails(contact: Contact): HeaderDetail[] {
		return [
			{
				label: "Owner",
				content: contact.owner!.caption,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={contact.owner!.picture}
						imgAlt={contact.owner!.caption}
						label={contact.owner!.caption}
					/>
				),
				link: "/user/" + contact.owner!.id
			},
			{
				label: "Account",
				content: contact.account?.caption,
				icon: <Icon category="standard" name="account" size="small" />,
				link: "/account/" + contact.account?.id
			},
			{ label: "Main Phone", content: contact.phone },
			{
				label: "Email",
				content: contact.email,
				link: "mailto:" + contact.email
			}
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
		if (this.activeLeftTabId === TAB.DETAILS) {
			this.doEditContact = true;
		} else if (this.activeLeftTabId === TAB.CHANNELS) {
			this.isChannelsModalOpen = true;
		} else if (this.activeLeftTabId === TAB.ADDRESSES) {
			this.isAddressesModalOpen = true;
		}
	};

	private cancelEditor = async () => {
		this.contactStore.cancel();
		this.doEditContact = false;
		this.isChannelsModalOpen = false;
		this.isAddressesModalOpen = false;
	};

	private closeEditor = async () => {
		try {
			const item = await this.contactStore.store();
			this.isChannelsModalOpen = false;
			this.isAddressesModalOpen = false;
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
