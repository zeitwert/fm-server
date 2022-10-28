
import { Avatar, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { EntityType, EntityTypeInfo, EntityTypes, session, User, UserInfo, UserStoreModel } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
import ItemEditor from "item/ui/ItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import UserStaticDataForm from "./forms/UserStaticDataForm";

enum LEFT_TABS {
	OVERVIEW = "static-data",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class UserPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.USER];

	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable userStore = UserStoreModel.create({});

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.userStore.load(this.props.params.userId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.userId !== prevProps.params.userId) {
			await this.userStore.load(this.props.params.userId!);
		}
	}

	render() {

		const user = this.userStore.user!;
		if (session.isNetworkActive) {
			return <Spinner variant="brand" size="large" />;
		} else if (!user) {
			return <NotFound entityType={this.entityType} id={this.props.params.userId!} />;
		}
		session.setHelpContext(`${EntityType.USER}-${this.activeLeftTabId}`);

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
					<ItemRightPart store={this.userStore} />
				</ItemGrid>
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
				content: owner.caption,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={owner.picture}
						imgAlt={owner.caption}
						label={owner.caption}
					/>
				),
				link: "/user/" + user.owner!.id
			},
			{ label: "Mandant", content: user.tenant?.name },
		];
	}

	private getHeaderActions() {
		return (
			<>
			</>
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

}

export default withRouter(UserPage);
