
import { Button, ButtonGroup, Modal, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { API, Building, BuildingElement, BuildingStore, BuildingStoreModel, Config, EntityType, EntityTypeInfo, EntityTypes, session } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import SidePanel from "app/ui/SidePanel";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import NotesTab from "lib/item/ui/tab/NotesTab";
import TasksTab from "lib/item/ui/tab/TasksTab";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import TabProjection from "lib/projection/ui/TabProjection";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import BuildingLocationForm from "./tabs/BuildingLocationForm";
import BuildingRatingForm from "./tabs/BuildingRatingForm";
import BuildingStaticDataForm from "./tabs/BuildingStaticDataForm";
import BuildingSummaryTab from "./tabs/BuildingSummaryTab";
import ElementRatingForm, { ElementAccessor } from "./tabs/ElementRatingForm";

enum LEFT_TABS {
	OVERVIEW = "static-data",
	LOCATION = "location",
	RATING = "rating",
	EVALUATION = "evaluation",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	DOCUMENTS = "documents",
	NOTES = "notes",
	ACTIVITIES = "activities",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class BuildingPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.BUILDING];

	@observable buildingStore: BuildingStore = BuildingStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.OVERVIEW;
	@observable activeRightTabId = RIGHT_TABS.DOCUMENTS;
	@observable currentElement: BuildingElement | undefined;
	@observable currentElementAccessor: ElementAccessor | undefined;
	@observable showConfirmation: boolean = false;
	@observable confirmationTitle: string = "";
	@observable confirmationDetails: string = "";
	@observable confirmationAction: () => void = () => { };

	@computed
	get hasValidations(): boolean {
		return this.buildingStore.building?.meta?.validationList?.length! > 0;
	}

	@computed
	get validationCount(): number {
		return this.buildingStore.building?.meta?.validationList?.length || 0;
	}

	@computed
	get hasErrors(): boolean {
		return this.buildingStore.building?.meta?.validationList?.filter(v => v.validationLevel?.id === "error").length! > 0;
	}

	@computed
	get hasCoverFoto(): boolean {
		return !!this.buildingStore.building?.coverFoto?.contentTypeId;
	}

	@computed
	get hasActiveRating(): boolean {
		return ["open", "review"].indexOf(this.buildingStore.building?.ratingStatus?.id || "") >= 0;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.buildingStore.load(this.props.params.buildingId!);
		session.setHelpContext(`${EntityType.BUILDING}-${this.activeLeftTabId}`);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.buildingId !== prevProps.params.buildingId) {
			await this.buildingStore.load(this.props.params.buildingId!);
		}
	}

	render() {

		const building = this.buildingStore.building!;
		if (!building && session.isNetworkActive) {
			return <></>;
		} else if (!building) {
			return <NotFound entityType={this.entityType} id={this.props.params.buildingId!} />;
		}

		const isFullWidth = [LEFT_TABS.RATING, LEFT_TABS.EVALUATION].indexOf(this.activeLeftTabId) >= 0;
		const allowEditStaticData = !building.ratingStatus || building.ratingStatus.id !== "review";
		const allowEditRating = !!building.ratingStatus && building.ratingStatus.id === "open";
		const isActive = !building.meta?.closedAt;
		const allowEdit = (allowEditStaticData && [LEFT_TABS.OVERVIEW, LEFT_TABS.LOCATION].indexOf(this.activeLeftTabId) >= 0) || (allowEditRating && [LEFT_TABS.RATING].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.buildingStore.notesStore.notes.length;
		const tasksCount = this.buildingStore.tasksStore.tasks.length;
		const missingDocument = !this.hasCoverFoto;

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
								<TabsPanel label="Stammdaten">
									{
										this.activeLeftTabId === LEFT_TABS.OVERVIEW &&
										<BuildingStaticDataForm store={this.buildingStore} />
									}
								</TabsPanel>
								<TabsPanel label={<span>Lage{!building.geoCoordinates && <abbr className="slds-required"> *</abbr>}</span>}>
									{
										this.activeLeftTabId === LEFT_TABS.LOCATION &&
										<BuildingLocationForm store={this.buildingStore} />
									}
								</TabsPanel>
								<TabsPanel label={<span>Bewertung{this.hasActiveRating && <abbr style={{ color: "#014486" }}> *</abbr>}</span>}>
									{
										this.activeLeftTabId === LEFT_TABS.RATING &&
										<BuildingRatingForm
											store={this.buildingStore}
											currentElementId={this.currentElement?.id}
											onOpenElementRating={this.onOpenElementRating}
											onCloseElementRating={this.onCloseElementRating}
										/>
									}
								</TabsPanel>
								<TabsPanel label="Auswertung" disabled={this.buildingStore.isInTrx || this.hasErrors} hasError={this.hasErrors}>
									{
										this.activeLeftTabId === LEFT_TABS.EVALUATION &&
										<TabProjection
											itemType="building"
											itemId={this.buildingStore.building?.id!}
										/>
									}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<ItemRightPart isFullWidth={isFullWidth}>
						<Tabs
							className="full-height"
							selectedIndex={RIGHT_TAB_VALUES.indexOf(this.activeRightTabId)}
							onSelect={(tabId: number) => (this.activeRightTabId = RIGHT_TAB_VALUES[tabId])}
						>
							<TabsPanel label={<span>Dokumente{missingDocument && <abbr className="slds-required"> *</abbr>}</span>}>
								{
									this.activeRightTabId === RIGHT_TABS.DOCUMENTS &&
									<BuildingSummaryTab building={building} afterSave={this.reload} />
								}
							</TabsPanel>
							<TabsPanel label={"Notizen" + (notesCount ? ` (${notesCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab
										relatedToId={this.buildingStore.id!}
										store={this.buildingStore.notesStore}
										notes={this.buildingStore.notesStore.notes}
									/>
								}
							</TabsPanel>
							<TabsPanel label={"Aufgaben" + (tasksCount ? ` (${tasksCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<TasksTab
										relatedToId={this.buildingStore.id!}
										store={this.buildingStore.tasksStore}
										tasks={this.buildingStore.tasksStore.tasks}
										onTaskStored={this.reload}
									/>
								}
							</TabsPanel>
							{
								this.hasValidations &&
								<TabsPanel label={"Validierungen" + (this.validationCount ? ` (${this.validationCount})` : "")}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={building.meta?.validationList!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					this.showConfirmation &&
					<Confirmation
						title={this.confirmationTitle}
						explanation={this.confirmationDetails}
						onCancel={() => this.showConfirmation = false}
						onOk={this.confirmationAction}
					/>
				}
				{
					this.activeLeftTabId === LEFT_TABS.RATING && !!this.currentElement && !!this.currentElementAccessor &&
					<SidePanel style={{ top: "110px", bottom: "30px", right: "30px", minWidth: "28rem" }}>
						<div onClick={(e: React.MouseEvent<HTMLDivElement>) => e.stopPropagation()}>
							<ElementRatingForm
								element={this.currentElement}
								elementAccessor={this.currentElementAccessor}
								onClose={this.onCloseElementRating}
							/>
						</div>
					</SidePanel>
				}
				{
					session.isNetworkActive &&
					<Spinner variant="brand" size="large" />
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
		// 				imgSrc={session.avatarUrl(building.owner!.id)}
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
		const building = this.buildingStore.building;
		if (!!building?.meta?.closedAt) {
			return (
				<ButtonGroup variant="list">
					<Button onClick={() => { }}>Immobilie reaktivieren</Button>
				</ButtonGroup>
			);
		}
		const isNew = this.buildingStore.isNew;
		const isInTrx = this.buildingStore.isInTrx;
		const ratingStatus = building?.ratingStatus;
		return (
			<>
				{
					!isInTrx && [LEFT_TABS.OVERVIEW, LEFT_TABS.LOCATION].indexOf(this.activeLeftTabId) >= 0 &&
					<ButtonGroup variant="list">
						<Button onClick={this.doExport}>Export</Button>
					</ButtonGroup>
				}
				{
					!isNew && !isInTrx &&
					<ButtonGroup variant="list">
						<Button variant="text-destructive" onClick={this.showDeleteConfirmation}>Immobilie löschen</Button>
					</ButtonGroup>
				}
				{
					!session.hasReadOnlyRole && !isInTrx && [LEFT_TABS.RATING].indexOf(this.activeLeftTabId) >= 0 &&
					<>
						{
							!ratingStatus &&
							<ButtonGroup variant="list">
								<Button variant="brand" onClick={this.addRating}>Neue Bewertung</Button>
							</ButtonGroup>
						}
						{
							ratingStatus?.id === "open" &&
							<ButtonGroup variant="list">
								<Button variant="text-destructive" onClick={this.showDiscardConfirmation}>Bewertung verwerfen</Button>
								<Button variant="brand" onClick={() => this.moveRatingStatus("review")} disabled={this.hasValidations}>Bewertung überprüfen</Button>
							</ButtonGroup>
						}
						{
							ratingStatus?.id === "review" &&
							<ButtonGroup variant="list">
								<Button onClick={() => this.moveRatingStatus("open")}>Bewertung zurückweisen</Button>
								<Button variant="brand" onClick={() => this.moveRatingStatus("done")}>Bewertung akzeptieren</Button>
							</ButtonGroup>
						}
						{
							ratingStatus?.id === "done" &&
							<>
								<ButtonGroup variant="list">
									<Button onClick={this.addRating}>Neue Bewertung</Button>
								</ButtonGroup>
								<ButtonGroup variant="list">
									<Button onClick={() => this.moveRatingStatus("open")}>Bewertung reaktivieren</Button>
								</ButtonGroup>
							</>
						}
					</>
				}
				{
					session.isAdvisorTenant && !this.hasErrors && !isInTrx && [LEFT_TABS.EVALUATION].indexOf(this.activeLeftTabId) >= 0 &&
					<ButtonGroup variant="list">
						<Button onClick={() => this.doGenDocx(building?.id!)}>Generate Word</Button>
					</ButtonGroup>
				}
			</>
		);
	}

	private openEditor = () => {
		this.buildingStore.edit();
	};

	private cancelEditor = async () => {
		this.buildingStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.buildingStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.buildingStore.load(this.props.params.buildingId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

	private doExport = async () => {
		const response = await API.get(Config.getRestUrl("building", "buildings/" + this.props.params.buildingId!));
		const objectUrl = window.URL.createObjectURL(new Blob([JSON.stringify(response.data, null, 2)]));
		const contentDisposition = response.headers["content-disposition"];
		const filename = contentDisposition?.match(/filename="(.+)"/)?.[1];
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

	private doGenDocx = (id: string) => {
		window.location.href = Config.getRestUrl("building", "buildings/" + id + "/evaluation?format=docx");
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

	private showDiscardConfirmation = () => {
		this.confirmationTitle = "Bewertung verwerfen";
		this.confirmationDetails = "Sie sind dabei die aktuelle Bewertung zu löschen.\nDie Daten der Bewertung gehen verloren.\nSind Sie sicher?";
		this.confirmationAction = () => { this.moveRatingStatus("discard", true); this.showConfirmation = false; };
		this.showConfirmation = true;
	}

	private showDeleteConfirmation = () => {
		this.confirmationTitle = "Immobilie löschen";
		this.confirmationDetails = "Sie sind dabei die aktuelle Immobilie zu löschen.\nSind Sie sicher?";
		this.confirmationAction = async () => {
			await this.buildingStore.delete();
			this.showConfirmation = false;
			window.location.replace("/building");
		};
		this.showConfirmation = true;
	}

	private reload = async () => {
		this.buildingStore.load(this.buildingStore.id!);
	};

	private onOpenElementRating = (element: BuildingElement, elementAccessor: ElementAccessor) => {
		this.currentElement = element;
		this.currentElementAccessor = elementAccessor;
	}

	private onCloseElementRating = () => {
		this.currentElement = undefined;
		this.currentElementAccessor = undefined;
	}

}

export default withRouter(BuildingPage);

interface ConfirmationProps {
	title: string;
	explanation: string;
	onOk: () => void;
	onCancel: () => void;
}

class Confirmation extends React.Component<ConfirmationProps> {

	render() {
		return <Modal
			isOpen={true}
			footer={[
				<Button label="Abbrechen" key="cancel" onClick={this.props.onCancel} />,
				<Button label="OK" variant="brand" key="ok" onClick={this.props.onOk} />,
			]}
			onRequestClose={this.props.onCancel}
			heading={this.props.title}
			prompt="warning"
		>
			<div className="slds-m-around_medium">
				{
					this.props.explanation.split("\n").map((x, index) => <p key={"part-" + index}>{x}</p>)
				}
			</div>
		</Modal>;
	}

}
