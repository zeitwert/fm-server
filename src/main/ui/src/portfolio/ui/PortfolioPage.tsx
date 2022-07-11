
import { Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { EntityType, Portfolio, PortfolioStoreModel, session } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import ItemEditor from "item/ui/ItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import TabProjection from "projection/ui/TabProjection";
import React from "react";
import PortfolioStaticDataForm from "./forms/PortfolioStaticDataForm";

enum TAB {
	DETAILS = 0,
	EVALUATION = 1,
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class PortfolioPage extends React.Component<RouteComponentProps> {

	@observable activeLeftTabId = TAB.DETAILS;
	@observable portfolioStore = PortfolioStoreModel.create({});

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.portfolioStore.load(this.props.params.portfolioId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.portfolioId !== prevProps.params.portfolioId) {
			await this.portfolioStore.load(this.props.params.portfolioId!);
		}
	}

	render() {
		const portfolio = this.portfolioStore.portfolio!;
		if ((session.isNetworkActive || !portfolio) && !this.portfolioStore.isInTrx) {
			return <Spinner variant="brand" size="large" />;
		}
		const isFullWidth = [TAB.DETAILS].indexOf(this.activeLeftTabId) < 0;
		const customEditorButtons = (
			<>
			</>
		);
		return (
			<>
				<ItemHeader
					store={this.portfolioStore}
					details={this.getHeaderDetails(portfolio)}
				/>
				<ItemGrid>
					<ItemLeftPart isFullWidth={isFullWidth}>
						<ItemEditor
							store={this.portfolioStore}
							entityType={EntityType.PORTFOLIO}
							showEditButtons={this.activeLeftTabId === TAB.DETAILS}
							customButtons={customEditorButtons}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
							key={"portfolio-" + this.portfolioStore.portfolio?.id}
						>
							<Tabs
								className="full-height"
								selectedIndex={this.activeLeftTabId}
								onSelect={(tabId: any) => (this.activeLeftTabId = tabId)}
							>
								<TabsPanel label="Details">
									{this.activeLeftTabId === TAB.DETAILS && <PortfolioStaticDataForm store={this.portfolioStore} />}
								</TabsPanel>
								<TabsPanel label="Auswertung">
									{this.activeLeftTabId === TAB.EVALUATION && <TabProjection itemType="portfolio" itemId={this.portfolioStore.portfolio?.id!} />}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<>
						{
							!isFullWidth &&
							<ItemRightPart store={this.portfolioStore} />
						}
					</>
				</ItemGrid>
			</>
		);
	}

	private getHeaderDetails(portfolio: Portfolio): HeaderDetail[] {
		return [];
		// return [
		// 	{
		// 		label: "Owner",
		// 		content: portfolio.owner!.caption,
		// 		icon: (
		// 			<Avatar
		// 				variant="user"
		// 				size="small"
		// 				imgSrc={portfolio.owner!.picture}
		// 				imgAlt={portfolio.owner!.caption}
		// 				label={portfolio.owner!.caption}
		// 			/>
		// 		),
		// 		link: "/user/" + portfolio.owner!.id
		// 	},
		// 	{
		// 		label: "Gemeinde",
		// 		content: portfolio.account?.caption,
		// 		icon: portfolio.account ? <Icon category="standard" name="account" size="small" /> : undefined,
		// 		link: portfolio.account ? "/account/" + portfolio.account?.id : undefined
		// 	}
		// ];
	}

	private openEditor = () => {
		this.portfolioStore.edit();
	};

	private cancelEditor = async () => {
		await this.portfolioStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.portfolioStore.store();
			this.ctx.showToast("success", `Portfolio stored`);
			return item;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Portfolio: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(PortfolioPage);
