
import { Button, ButtonGroup } from "@salesforce/design-system-react";
import { Config, EntityType, Enumerated, Portfolio, PortfolioStore, PortfolioStoreModel, session } from "@zeitwert/ui-model";
import { RouteComponentProps } from "app/frame/withRouter";
import ItemsPage from "lib/item/ui/ItemsPage";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import PortfolioCreationForm from "./PortfolioCreationForm";
import PortfolioPage from "./PortfolioPage";

const portfolioStore = PortfolioStoreModel.create({});

@observer
export default class PortfolioArea extends React.Component<RouteComponentProps> {

	@observable selection: string[] = [];

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount(): void {
		session.setHelpContext(EntityType.PORTFOLIO);
	}

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
							customActions={this.getHeaderActions()}
							canCreate={session.isUser && !session.hasReadOnlyRole}
							createEditor={() => <PortfolioCreationForm portfolio={portfolioStore.portfolio!} />}
							onAfterCreate={(store: PortfolioStore) => { initPortfolio(store.portfolio!, session.sessionInfo?.account) }}
							onSelectionChange={this.onSelectionChange}
						/>
					}
				/>
				<Route path=":portfolioId" element={<PortfolioPage />} />
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
			</>
		);
	}

	private onSelectionChange = (selectedItems: any[]) => {
		this.selection = selectedItems.map(s => s.id);
	};

	private printEvaluations = () => {
		window.location.href = Config.getRestUrl("portfolio", "portfolios/" + this.selection.join(",") + "/evaluation?format=pdf");
	};

}

const initPortfolio = (portfolio: Portfolio, account: Enumerated | undefined) => {
	portfolio.setField("account", account?.id);
}
