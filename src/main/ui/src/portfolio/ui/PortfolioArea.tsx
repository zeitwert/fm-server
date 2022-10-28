
import { AccountInfo, EntityType, Portfolio, PortfolioStore, PortfolioStoreModel, session } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import ItemsPage from "item/ui/ItemsPage";
import { inject, observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import PortfolioCreationForm from "./forms/PortfolioCreationForm";
import PortfolioPage from "./PortfolioPage";

const portfolioStore = PortfolioStoreModel.create({});

@inject("appStore", "session", "showAlert", "showToast")
@observer
export default class PortfolioArea extends React.Component {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		session.setHelpContext(EntityType.PORTFOLIO);
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
							canCreate={session.isUser && !session.hasReadOnlyRole}
							createEditor={() => <PortfolioCreationForm store={portfolioStore} />}
							onAfterCreate={(store: PortfolioStore) => { initPortfolio(store.item!, this.ctx.session.sessionInfo?.account) }}
						/>
					}
				/>
				<Route path=":portfolioId" element={<PortfolioPage />} />
			</Routes>
		);
	}

}

const initPortfolio = (portfolio: Portfolio, account: AccountInfo | undefined) => {
	portfolio.setField("account", account?.id);
}
