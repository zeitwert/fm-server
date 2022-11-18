
import { Avatar, Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { AccountStoreModel, EntityType, EntityTypeInfo, EntityTypes, Enumerated, session, Tenant, TenantStoreModel, UserInfo, UserStoreModel } from "@zeitwert/ui-model";
import AccountCreationForm from "account/ui/forms/AccountCreationForm";
import { ActivityPortlet } from "activity/ActivityPortlet";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
import ItemEditor from "item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import ItemModal from "item/ui/ItemModal";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemPage";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import UserCreationForm from "user/ui/forms/UserCreationForm";
import TenantStaticDataForm from "./forms/TenantStaticDataForm";
import TenantSummaryForm from "./forms/TenantSummaryForm";

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
class TenantPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.TENANT];

	@observable tenantStore = TenantStoreModel.create({});
	@observable accountStore = AccountStoreModel.create({});
	@observable userStore = UserStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable activeRightTabId = RIGHT_TABS.SUMMARY;

	@computed
	get hasLogo(): boolean {
		return !!this.tenantStore.tenant?.logo?.contentTypeId;
	}

	@computed
	get hasBanner(): boolean {
		return !!this.tenantStore.tenant?.banner?.contentTypeId;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.tenantStore.load(this.props.params.tenantId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.tenantId !== prevProps.params.tenantId) {
			await this.tenantStore.load(this.props.params.tenantId!);
		}
	}

	render() {

		const tenant = this.tenantStore.tenant!;
		if (session.isNetworkActive) {
			return <Spinner variant="brand" size="large" />;
		} else if (!tenant) {
			return <NotFound entityType={this.entityType} id={this.props.params.tenantId!} />;
		}
		session.setHelpContext(`${EntityType.TENANT}-${this.activeLeftTabId}`);

		const allowEditStaticData = session.isAdmin;
		const isActive = !tenant.meta?.closedAt;
		const allowEdit = (allowEditStaticData && [LEFT_TABS.OVERVIEW].indexOf(this.activeLeftTabId) >= 0);

		return (
			<>
				<ItemHeader
					store={this.tenantStore}
					details={this.getHeaderDetails(tenant)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart>
						<ItemEditor
							key={"tenant-" + this.tenantStore.tenant?.id}
							store={this.tenantStore}
							entityType={EntityType.TENANT}
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
									{this.activeLeftTabId === LEFT_TABS.OVERVIEW && <TenantStaticDataForm store={this.tenantStore} />}
								</TabsPanel>
								{
									/*
								<TabsPanel label={"Cases (" + this.tenantStore.counters?.docCount + ")"}>
									{this.activeLeftTabId === LEFT_TABS.CASES && (
										<TenantTabOrders
											tenant={this.tenantStore.tenant!}
											template="doc.docs.by-tenant"
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
							<TabsPanel label={<span>Steckbrief{(!this.hasLogo || !this.hasBanner) && <abbr className="slds-required"> *</abbr>}</span>}>
								{
									this.activeRightTabId === RIGHT_TABS.SUMMARY &&
									<TenantSummaryForm tenant={tenant} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITY &&
									<ActivityPortlet {...Object.assign({}, this.props, { item: tenant, onSave: () => null as unknown as Promise<any> })} />
								}
							</TabsPanel>
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					this.accountStore.isInTrx &&
					<ItemModal
						store={this.accountStore}
						entityType={EntityType.ACCOUNT}
						onClose={this.closeAccountEditor}
						onCancel={this.cancelAccountEditor}
					>
						{() => <AccountCreationForm store={this.accountStore} />}
					</ItemModal>

				}
				{
					this.userStore.isInTrx &&
					<ItemModal
						store={this.userStore}
						entityType={EntityType.USER}
						onClose={this.closeUserEditor}
						onCancel={this.cancelUserEditor}
					>
						{() => <UserCreationForm store={this.userStore} />}
					</ItemModal>
				}
			</>
		);
	}

	private getHeaderDetails(tenant: Tenant): HeaderDetail[] {
		const tenantOwner: UserInfo = tenant.owner as UserInfo;
		return [
			{ label: "Type", content: tenant.tenantType?.name },
			{
				label: "Owner",
				content: tenantOwner.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(tenantOwner.id)}
						imgAlt={tenantOwner.name}
						label={tenantOwner.name}
					/>
				),
				link: "/user/" + tenant.owner!.id
			},
		];
	}

	private getHeaderActions() {
		return (
			<>
				<ButtonGroup variant="list">
					<Button onClick={this.openUserEditor}>Add User</Button>
					<Button onClick={this.openAccountEditor}>Add Account</Button>
				</ButtonGroup>
			</>
		);
	}

	private openEditor = () => {
		this.tenantStore.edit();
	};

	private cancelEditor = async () => {
		this.tenantStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.tenantStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.tenantStore.load(this.props.params.tenantId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

	private openAccountEditor = () => {
		const tenant = this.tenantStore.item!;
		const tenantEnum: Enumerated = {
			id: tenant.id,
			name: tenant.caption!
		};
		this.accountStore.create({
			tenant: tenantEnum,
			owner: this.ctx.session.sessionInfo!.user
		});
	};

	private cancelAccountEditor = async () => {
		this.accountStore.cancel();
	};

	private closeAccountEditor = async () => {
		try {
			await this.accountStore.store();
			this.ctx.showToast("success", `New User ${this.userStore.id} created`);
			this.props.navigate("/account/" + this.accountStore.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not create new Account: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

	private openUserEditor = () => {
		const tenant = this.tenantStore.item!;
		const tenantEnum: Enumerated = {
			id: tenant.id,
			name: tenant.caption!
		};
		this.userStore.create({
			tenant: tenantEnum,
			owner: this.ctx.session.sessionInfo!.user,
			role: "user"
		});
	};

	private cancelUserEditor = async () => {
		this.userStore.cancel();
	};

	private closeUserEditor = async () => {
		try {
			await this.userStore.store();
			this.ctx.showToast("success", `New User ${this.userStore.id} created`);
			this.props.navigate("/user/" + this.userStore.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not create new User: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

	private reload = async () => {
		this.tenantStore.load(this.tenantStore.id!);
	};

}

export default withRouter(TenantPage);
