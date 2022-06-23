
import { Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Building, BuildingStore, BuildingStoreModel, Config, EntityType } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import ItemEditor from "item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemPage";
import ErrorTab from "item/ui/tab/ErrorTab";
import NotesTab from "item/ui/tab/NotesTab";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import TabProjection from "projection/ui/TabProjection";
import React from "react";
import BuildingLocationForm from "./forms/BuildingLocationForm";
import BuildingRatingForm from "./forms/BuildingRatingForm";
import BuildingStaticDataForm from "./forms/BuildingStaticDataForm";
import BuildingSummaryForm from "./forms/BuildingSummaryForm";

enum LEFT_TABS {
	OVERVIEW = 0,
	LOCATION = 1,
	RATING = 2,
	EVALUATION = 3,
}

enum RIGHT_TABS {
	SUMMARY = 0,
	NOTES = 1,
	//ACTIVITY = 1,
	ERRORS = 2,
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class BuildingPage extends React.Component<RouteComponentProps> {

	@observable buildingStore: BuildingStore = BuildingStoreModel.create({});
	@observable isLoaded = false;
	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable activeRightTabId = RIGHT_TABS.SUMMARY;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.buildingStore.load(this.props.params.buildingId!);
		this.isLoaded = true;
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.buildingId !== prevProps.params.buildingId) {
			await this.buildingStore.load(this.props.params.buildingId!);
			this.isLoaded = true;
		}
	}

	render() {
		if (!this.isLoaded) {
			return <Spinner variant="brand" size="large" />;
		}
		const building = this.buildingStore.building!;
		const isFullWidth = [LEFT_TABS.RATING, LEFT_TABS.EVALUATION].indexOf(this.activeLeftTabId) >= 0;
		const allowEdit = [LEFT_TABS.OVERVIEW, LEFT_TABS.LOCATION, LEFT_TABS.RATING].indexOf(this.activeLeftTabId) >= 0;
		const hasError = building.meta?.validationList.length! > 0;
		return (
			<>
				<ItemHeader
					store={this.buildingStore}
					details={this.getHeaderDetails(building)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart isFullWidth={isFullWidth}>
						<ItemEditor
							key={"building-" + this.buildingStore.building?.id}
							store={this.buildingStore}
							entityType={EntityType.BUILDING}
							showEditButtons={allowEdit}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
						>
							<Tabs
								className="full-height"
								selectedIndex={this.activeLeftTabId}
								onSelect={(tabId: number) => (this.activeLeftTabId = tabId)}
							>
								<TabsPanel label="Stammdaten" classname="abc">
									{this.activeLeftTabId === LEFT_TABS.OVERVIEW && <BuildingStaticDataForm store={this.buildingStore} />}
								</TabsPanel>
								<TabsPanel label="Lage">
									{this.activeLeftTabId === LEFT_TABS.LOCATION && <BuildingLocationForm store={this.buildingStore} />}
								</TabsPanel>
								<TabsPanel label="Bewertung">
									{this.activeLeftTabId === LEFT_TABS.RATING && <BuildingRatingForm store={this.buildingStore} />}
								</TabsPanel>
								<TabsPanel label="Auswertung" disabled={hasError} hasError={hasError}>
									{this.activeLeftTabId === LEFT_TABS.EVALUATION && <TabProjection itemType="building" itemId={this.buildingStore.building?.id!} />}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<ItemRightPart isFullWidth={isFullWidth}>
						<Tabs
							className="full-height"
							selectedIndex={this.activeRightTabId}
							onSelect={(tabId: number) => (this.activeRightTabId = tabId)}
						>
							<TabsPanel label="Steckbrief">
								{
									this.activeRightTabId === RIGHT_TABS.SUMMARY &&
									<BuildingSummaryForm building={building} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label={"Notizen"}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab
										relatedToId={this.buildingStore.id!}
										store={this.buildingStore.notesStore}
										notes={this.buildingStore.notesStore.notes}
									/>
								}
							</TabsPanel>
							{
								/*
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITY &&
									<ActivityPortlet {...Object.assign({}, this.props, { item: building, onSave: this.onSavePortlet })} />
								}
							</TabsPanel>
								*/
							}
							<TabsPanel label="Fehler" disabled={!hasError} hasError={hasError}>
								{
									this.activeRightTabId === RIGHT_TABS.ERRORS &&
									<ErrorTab validationList={building.meta?.validationList!} />
								}
							</TabsPanel>
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
			</>
		);
	}

	private getHeaderDetails(building: Building): HeaderDetail[] {
		return [];
		// return [
		// 	{
		// 		label: "Owner",
		// 		content: building.owner!.caption,
		// 		icon: (
		// 			<Avatar
		// 				variant="user"
		// 				size="small"
		// 				imgSrc={building.owner!.picture}
		// 				imgAlt={building.owner!.caption}
		// 				label={building.owner!.caption}
		// 			/>
		// 		),
		// 		link: "/user/" + building.owner!.id
		// 	},
		// 	{
		// 		label: "Account",
		// 		content: building.account?.caption,
		// 		icon: <Icon category="standard" name="account" size="small" />,
		// 		link: "/account/" + building.account?.id
		// 	},
		// 	{ label: "Address", content: `${building.street} ${building.zip} ${building.city}` }
		// ];
	}

	private getHeaderActions() {
		return (
			<>
				<ButtonGroup variant="list">
					<Button onClick={() => { window.location.href = Config.getTransferUrl("building", "buildings/" + this.props.params.buildingId!); }}>Export</Button>
				</ButtonGroup>
			</>
		);
	}

	private openEditor = () => {
		this.buildingStore.edit();
	};

	private cancelEditor = async () => {
		await this.buildingStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.buildingStore.store();
			this.ctx.showToast("success", `Building stored`);
			return item;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Building: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

	private reload = async () => {
		this.buildingStore.load(this.buildingStore.id!);
		// brute force reload
		// window.location.href = "/building/" + this.props.params.buildingId + "?t=" + (new Date()).getTime();
	};

	// private onSavePortlet = async (type: string, data: any) => {
	// 	let store: DocStore, payload: any, title: string;
	// 	switch (type) {
	// 		case ActivityFormTypes.TASK:
	// 			title = "Task";
	// 			store = TaskStoreModel.create({});
	// 			payload = FormParser.parseTask(data, this.ctx.session.sessionInfo!.user);
	// 			break;
	// 		default:
	// 			throw new Error("Undefined store set");
	// 	}
	// 	try {
	// 		store.create(payload);
	// 		await store.store();
	// 		this.ctx.showToast("success", title + " stored");
	// 	} catch (error: any) {
	// 		this.ctx.showAlert(
	// 			"error",
	// 			"Could not store " + title + ": " + (error.detail ? error.detail : error.title ? error.title : error)
	// 		);
	// 	}
	// };

}

export default withRouter(BuildingPage);
