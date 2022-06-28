import { Button } from "@salesforce/design-system-react";
import { AccountInfo, API, Building, BuildingStore, BuildingStoreModel, Config, EntityType, session } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import SidePanel from "frame/ui/SidePanel";
import ItemsPage from "item/ui/ItemsPage";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import BuildingPage from "./BuildingPage";
import BuildingPreview from "./BuildingPreview";
import BuildingCreationForm from "./forms/BuildingCreationForm";
import BuildingImportForm from "./forms/BuildingImportForm";

const buildingStore = BuildingStoreModel.create({});

@inject("appStore", "session", "showAlert", "showToast")
@observer
export default class BuildingArea extends React.Component {

	@observable showPreview = false;
	@observable previewItemId: string | undefined;
	@observable doImport = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
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
								actionButtons={this.getHeaderActions()}
								canCreate
								createEditor={() => <BuildingCreationForm store={buildingStore} />}
								onAfterCreate={(store: BuildingStore) => { initBuilding(store.item!, this.ctx.session.sessionInfo?.account) }}
								onOpenPreview={this.openPreview}
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
		if (session.isAdmin) {
			return [<Button key="import" label={"Import Immobilie"} onClick={this.openImport} />];
		} else {
			return [];
		}
	}

	private openPreview = (itemId: string) => {
		this.showPreview = true;
		this.previewItemId = itemId;
	};

	private closePreview = () => {
		this.showPreview = false;
		this.previewItemId = undefined;
	};

	private openImport = () => {
		this.doImport = true;
	};

	private cancelImport = async () => {
		this.doImport = false;
	};

	private onImport = async (content: string) => {
		try {
			const rsp = await API.post(Config.getTransferUrl("building", "buildings"), content, { headers: { "Content-Type": "application/json" } });
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

const initBuilding = (building: Building, account: AccountInfo | undefined) => {
	building.setField("account", account?.id);
	building.setField("country", { id: "ch", name: "Switzerland" });
	building.setField("currency", { id: "chf", name: "CHF" });
	building.setField("ratingStatus", { id: "open", name: "Open" });
}
