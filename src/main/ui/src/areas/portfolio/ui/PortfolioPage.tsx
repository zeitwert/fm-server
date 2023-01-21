
import { Button, ButtonGroup, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Config, EntityType, EntityTypeInfo, EntityTypes, Portfolio, PortfolioStoreModel, session } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import { ActivityPortlet } from "lib/activity/ActivityPortlet";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import TabProjection from "lib/projection/ui/TabProjection";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import PortfolioMainForm from "./tabs/PortfolioMainForm";

enum LEFT_TABS {
	MAIN = "main",
	EVALUATION = "evaluation",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	ACTIVITIES = "activities",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class PortfolioPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.PORTFOLIO];

	@observable portfolioStore = PortfolioStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.MAIN;
	@observable activeRightTabId = RIGHT_TABS.ACTIVITIES;

	@computed
	get hasValidations(): boolean {
		return this.portfolioStore.portfolio?.meta?.validationList?.length! > 0;
	}

	@computed
	get validationCount(): number {
		return this.portfolioStore.portfolio?.meta?.validationList?.length || 0;
	}

	@computed
	get hasErrors(): boolean {
		return this.portfolioStore.portfolio?.meta?.validationList?.filter(v => v.validationLevel?.id === "error").length! > 0;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.portfolioStore.load(this.props.params.portfolioId!);
		session.setHelpContext(`${EntityType.PORTFOLIO}-${this.activeLeftTabId}`);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.portfolioId !== prevProps.params.portfolioId) {
			await this.portfolioStore.load(this.props.params.portfolioId!);
		}
	}

	render() {

		const portfolio = this.portfolioStore.portfolio!;
		if (!portfolio && session.isNetworkActive) {
			return <></>;
		} else if (!portfolio) {
			return <NotFound entityType={this.entityType} id={this.props.params.portfolioId!} />;
		}

		const isFullWidth = [LEFT_TABS.EVALUATION].indexOf(this.activeLeftTabId) >= 0;
		const isActive = !portfolio.meta?.closedAt;
		const allowEdit = [LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0;

		const customEditorButtons = (
			<>
			</>
		);

		return (
			<>
				<ItemHeader
					store={this.portfolioStore}
					details={this.getHeaderDetails(portfolio)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart isFullWidth={isFullWidth}>
						<ItemEditor
							key={"portfolio-" + this.portfolioStore.portfolio?.id}
							store={this.portfolioStore}
							entityType={EntityType.PORTFOLIO}
							showEditButtons={isActive && allowEdit && !session.hasReadOnlyRole}
							customButtons={customEditorButtons}
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
										this.activeLeftTabId === LEFT_TABS.MAIN &&
										<PortfolioMainForm portfolio={this.portfolioStore.portfolio!} doEdit={this.portfolioStore.isInTrx} />
									}
								</TabsPanel>
								<TabsPanel label="Auswertung">
									{
										this.activeLeftTabId === LEFT_TABS.EVALUATION &&
										<TabProjection
											itemType="portfolio"
											itemId={this.portfolioStore.portfolio?.id!}
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
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<ActivityPortlet {...Object.assign({}, this.props, { item: portfolio, onSave: async () => { } })} />
								}
							</TabsPanel>
							{
								this.hasValidations &&
								<TabsPanel label={"Validierungen" + (this.validationCount ? ` (${this.validationCount})` : "")}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={portfolio.meta?.validationList!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					session.isNetworkActive &&
					<Spinner variant="brand" size="large" />
				}
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
		// 				imgSrc={session.avatarUrl(portfolio.owner!.id)}
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

	private getHeaderActions() {
		const portfolio = this.portfolioStore.portfolio;
		if (!!portfolio?.meta?.closedAt) {
			return (
				<ButtonGroup variant="list">
					<Button onClick={() => { }}>Portfolio reaktivieren</Button>
				</ButtonGroup>
			);
		}
		const isInTrx = this.portfolioStore.isInTrx;
		return (
			<>
				{
					session.isAdvisorTenant && !this.hasErrors && !isInTrx && [LEFT_TABS.EVALUATION].indexOf(this.activeLeftTabId) >= 0 &&
					<ButtonGroup variant="list">
						<Button onClick={() => this.doGenDocx(portfolio?.id!)}>Generate Word</Button>
					</ButtonGroup>
				}
			</>
		);
	}

	private openEditor = () => {
		this.portfolioStore.edit();
	};

	private cancelEditor = async () => {
		this.portfolioStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.portfolioStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.portfolioStore.load(this.props.params.portfolioId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

	private doGenDocx = (id: string) => {
		window.location.href = Config.getRestUrl("portfolio", "portfolios/" + id + "/evaluation?format=docx");
	}

}

export default withRouter(PortfolioPage);
