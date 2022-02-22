
import { EntityType, Portfolio, PortfolioStore, PortfolioStoreModel } from "@comunas/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import PortfolioCreationForm from "./forms/PortfolioCreationForm";
import PortfolioPage from "./PortfolioPage";

const portfolioStore = PortfolioStoreModel.create({});

export default class PortfolioArea extends React.Component {
	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.PORTFOLIO}
							store={portfolioStore}
							listDatamart="portfolio.portfolios"
							listTemplate="portfolio.portfolios.all"
							canCreate
							createEditor={() => <PortfolioCreationForm store={portfolioStore} />}
							onAfterCreate={(store: PortfolioStore) => { initPortfolio(store.item!) }}
						/>
					}
				/>
				<Route path=":portfolioId" element={<PortfolioPage />} />
			</Routes>
		);
	}
}

const initPortfolio = (portfolio: Portfolio) => {
	//portfolio.setField("country", { id: "ch", name: "Switzerland" });
	//portfolio.setField("currency", { id: "chf", name: "CHF" });
}
