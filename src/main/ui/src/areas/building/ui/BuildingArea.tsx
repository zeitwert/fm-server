
import { Button, ButtonGroup } from "@salesforce/design-system-react";
import { API, Building, BuildingStore, BuildingStoreModel, Config, EntityType, Enumerated, session } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import SidePanel from "app/ui/SidePanel";
import ItemsPage from "lib/item/ui/ItemsPage";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import BuildingCreationForm from "./BuildingCreationForm";
import BuildingPage from "./BuildingPage";
import BuildingPreview from "./BuildingPreview";
import BuildingImportForm from "./modals/BuildingImportForm";

const buildingStore = BuildingStoreModel.create({});

@inject("showAlert", "showToast")
@observer
class BuildingArea extends React.Component<RouteComponentProps> {

	@observable showPreview = false;
	@observable previewItemId: string | undefined;
	@observable selection: string[] = [];
	@observable doImport = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount(): void {
		session.setHelpContext(EntityType.BUILDING);
	}

	componentDidUpdate(prevProps: RouteComponentProps) {
		// close preview on route change
		if (this.props.location !== prevProps.location) {
			this.showPreview = false;
			this.previewItemId = undefined;
		}
	}

	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<>
							<ItemsPage
								entityType={EntityType.BUILDING}
								store={buildingStore}
								listDatamart="building.buildings"
								listTemplate="building.buildings.all"
								customActions={this.getHeaderActions()}
								canCreate={session.isUser && !session.hasReadOnlyRole}
								createEditor={() => <BuildingCreationForm building={buildingStore.building!} />}
								onAfterCreate={(store: BuildingStore) => { initBuilding(store.building!, session.sessionInfo?.account) }}
								onOpenPreview={this.openPreview}
								onSelectionChange={this.onSelectionChange}
							/>
							{
								this.doImport && (
									<BuildingImportForm
										onCancel={this.cancelImport}
										onImport={this.onImport}
									/>
								)
							}
							{
								this.showPreview && this.previewItemId &&
								<SidePanel>
									<BuildingPreview buildingId={this.previewItemId} onClose={this.closePreview} />
								</SidePanel>
							}
						</>
					}
				/>
				<Route path=":buildingId" element={<BuildingPage />} />
			</Routes>
		);
	}

	private getHeaderActions() {
		return (
			<>
				{
					!!this.selection.length &&
					<ButtonGroup variant="list">
						<Button key="print" label={"Bewertungen drucken"} onClick={this.printEvaluations} />
					</ButtonGroup>
				}
				{
					session.hasSuperUserRole &&
					<ButtonGroup variant="list">
						<Button key="import" label={"Import Immobilie"} onClick={this.openImport} />
					</ButtonGroup>
				}
			</>
		);
	}

	private openPreview = (itemId: string) => {
		if (this.previewItemId === itemId) {
			this.showPreview = false;
			this.previewItemId = undefined;
		} else {
			this.showPreview = true;
			this.previewItemId = itemId;
		}
	};

	private closePreview = () => {
		this.showPreview = false;
		this.previewItemId = undefined;
	};

	private onSelectionChange = (selectedItems: any[]) => {
		this.selection = selectedItems.map(s => s.id);
	};

	private printEvaluations = () => {
		window.location.href = Config.getRestUrl("building", "buildings/" + this.selection.join(",") + "/evaluation?format=pdf");
	};

	private openImport = () => {
		this.doImport = true;
	};

	private cancelImport = async () => {
		this.doImport = false;
	};

	private onImport = async (content: string) => {
		try {
			const rsp = await API.post(Config.getRestUrl("building", "buildings"), content, { headers: { "Content-Type": "application/json" } });
			this.doImport = false;
			this.ctx.showToast("success", `Building imported`);
			window.location.href = "building/" + rsp.data.id;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not import Building: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(BuildingArea);

const initBuilding = (building: Building, account: Enumerated | undefined) => {
	building.setField("account", account?.id);
	building.setField("country", { id: "ch", name: "Switzerland" });
	building.setField("currency", { id: "chf", name: "CHF" });
	building.setField("ratingStatus", { id: "open", name: "Open" });
}
