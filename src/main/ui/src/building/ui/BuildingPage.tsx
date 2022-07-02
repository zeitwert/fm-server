
import { Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { API, Building, BuildingStore, BuildingStoreModel, Config, EntityType, session } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import SidePanel from "frame/ui/SidePanel";
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
import ElementRatingForm from "./forms/ElementRatingForm";

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
	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable activeRightTabId = RIGHT_TABS.SUMMARY;
	@observable currentElement: any;
	@observable currentElementForm: any;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.buildingStore.load(this.props.params.buildingId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.buildingId !== prevProps.params.buildingId) {
			await this.buildingStore.load(this.props.params.buildingId!);
		}
	}

	render() {

		const building = this.buildingStore.building!;
		if (session.isNetworkActive || !building) {
			return <Spinner variant="brand" size="large" />;
		}

		const isFullWidth = [LEFT_TABS.RATING, LEFT_TABS.EVALUATION].indexOf(this.activeLeftTabId) >= 0;
		const allowEditStaticData = !building.ratingStatus || building.ratingStatus.id !== "review";
		const allowEditRating = building.ratingStatus && building.ratingStatus.id === "open";
		const allowEdit = ([LEFT_TABS.OVERVIEW, LEFT_TABS.LOCATION].indexOf(this.activeLeftTabId) >= 0 && allowEditStaticData) || ([LEFT_TABS.RATING].indexOf(this.activeLeftTabId) >= 0 && allowEditRating);
		const hasValidation = building.meta?.validationList?.length! > 0;
		const hasError = building.meta?.validationList?.filter(v => v.validationLevel?.id === "error").length! > 0;

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
									{
										this.activeLeftTabId === LEFT_TABS.OVERVIEW &&
										<BuildingStaticDataForm store={this.buildingStore} />
									}
								</TabsPanel>
								<TabsPanel label="Lage">
									{
										this.activeLeftTabId === LEFT_TABS.LOCATION &&
										<BuildingLocationForm store={this.buildingStore} />
									}
								</TabsPanel>
								<TabsPanel label="Bewertung">
									{
										this.activeLeftTabId === LEFT_TABS.RATING &&
										<BuildingRatingForm
											store={this.buildingStore}
											onOpenElementRating={this.onOpenElementRating}
											onCloseElementRating={this.onCloseElementRating}
										/>
									}
								</TabsPanel>
								<TabsPanel label="Auswertung" disabled={hasError} hasError={hasError}>
									{
										this.activeLeftTabId === LEFT_TABS.EVALUATION &&
										<TabProjection itemType="building" itemId={this.buildingStore.building?.id!} />
									}
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
							<TabsPanel label="Fehler" disabled={!hasValidation} hasError={hasValidation}>
								{
									this.activeRightTabId === RIGHT_TABS.ERRORS &&
									<ErrorTab validationList={building.meta?.validationList!} />
								}
							</TabsPanel>
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					!!this.currentElement &&
					<SidePanel style={{ top: "110px", bottom: "30px", right: "30px", minWidth: "28rem" }}>
						<div onClick={(e: React.MouseEvent<HTMLDivElement>) => e.stopPropagation()}>
							<ElementRatingForm
								element={this.currentElement}
								elementForm={this.currentElementForm}
								onClose={this.onCloseElementRating}
							/>
						</div>
					</SidePanel>
				}
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
		const ratingStatus = this.buildingStore.building?.ratingStatus;
		const hasErrors = (this.buildingStore.building?.meta?.validationList?.length! > 0) || false;
		return (
			<>
				{
					!this.buildingStore.isInTrx && [LEFT_TABS.OVERVIEW, LEFT_TABS.LOCATION].indexOf(this.activeLeftTabId) >= 0 &&
					<ButtonGroup variant="list">
						<Button onClick={this.doExport}>Export</Button>
					</ButtonGroup>
				}
				{
					!this.buildingStore.isInTrx && ratingStatus?.id === "open" &&
					<ButtonGroup variant="list">
						<Button onClick={() => this.moveRatingStatus("discard", true)}>Bewertung verwerfen</Button>
						<Button variant="brand" onClick={() => this.moveRatingStatus("review")} disabled={hasErrors}>Bewertung überprüfen</Button>
					</ButtonGroup>
				}
				{
					!this.buildingStore.isInTrx && ratingStatus?.id === "review" &&
					<ButtonGroup variant="list">
						<Button onClick={() => this.moveRatingStatus("open")}>Bewertung zurückweisen</Button>
						<Button variant="brand" onClick={() => this.moveRatingStatus("done")}>Bewertung akzeptieren</Button>
					</ButtonGroup>
				}
				{
					!this.buildingStore.isInTrx && [LEFT_TABS.RATING].indexOf(this.activeLeftTabId) >= 0 &&
					<>
						{
							!ratingStatus &&
							<ButtonGroup variant="list">
								<Button variant="brand" onClick={this.addRating}>Neue Bewertung</Button>
							</ButtonGroup>
						}
						{
							ratingStatus?.id === "done" &&
							<ButtonGroup variant="list">
								<Button onClick={this.addRating}>Neue Bewertung</Button>
							</ButtonGroup>
						}
						{
							ratingStatus?.id === "done" &&
							<ButtonGroup variant="list">
								<Button onClick={() => this.moveRatingStatus("open")}>Bewertung reaktivieren</Button>
							</ButtonGroup>
						}
					</>
				}
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

	private doExport = async () => {
		const response = await API.get(Config.getTransferUrl("building", "buildings/" + this.props.params.buildingId!));
		const objectUrl = window.URL.createObjectURL(new Blob([JSON.stringify(response.data, null, 2)]));
		const contentDisposition = response.headers["content-disposition"];
		const filename = contentDisposition.match(/filename="(.+)"/)?.[1];
		if (filename) {
			const anchor = document.createElement("a");
			document.body.appendChild(anchor);
			anchor.href = objectUrl;
			anchor.download = filename;
			anchor.click();
			document.body.removeChild(anchor);
			window.URL.revokeObjectURL(objectUrl);
		}
	}

	private addRating = () => {
		this.buildingStore.building?.addRating();
	};

	private moveRatingStatus = async (ratingStatus: string, reload: boolean = false) => {
		await this.buildingStore.building?.moveRatingStatus(ratingStatus);
		if (reload) {
			this.reload();
		}
	};

	private reload = async () => {
		this.buildingStore.load(this.buildingStore.id!);
	};

	private onOpenElementRating = (element: any, elementForm: any) => {
		this.currentElement = element;
		this.currentElementForm = elementForm;
	}

	private onCloseElementRating = () => {
		this.currentElement = undefined;
		this.currentElementForm = undefined;
	}

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
