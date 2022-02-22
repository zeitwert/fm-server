import { Building, BuildingStore, BuildingStoreModel, EntityType } from "@comunas/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import BuildingPage from "./BuildingPage";
import BuildingCreationForm from "./forms/BuildingCreationForm";

const buildingStore = BuildingStoreModel.create({});

export default class BuildingArea extends React.Component {
	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.BUILDING}
							store={buildingStore}
							listDatamart="building.buildings"
							listTemplate="building.buildings.all"
							canCreate
							createEditor={() => <BuildingCreationForm store={buildingStore} />}
							onAfterCreate={(store: BuildingStore) => { initBuilding(store.item!) }}
						/>
					}
				/>
				<Route path=":buildingId" element={<BuildingPage />} />
			</Routes>
		);
	}
}

const initBuilding = (building: Building) => {
	building.setField("country", { id: "ch", name: "Switzerland" });
	building.setField("currency", { id: "chf", name: "CHF" });
}
