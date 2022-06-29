
import { Avatar, ButtonGroup, Icon, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import {
	CaseStage, EntityType, Lead,
	LeadStore,
	LeadStoreModel,
	session
} from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { StageSelector } from "doc/ui/StageSelector";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import FormItemEditor from "item/ui/FormItemEditor";
import ItemDetailView from "item/ui/ItemDetailView";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import ItemPath from "item/ui/ItemPath";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

enum TAB {
	DETAILS = 0,
	ACCOUNT = 1
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class LeadPage extends React.Component<RouteComponentProps> {

	@observable activeLeftTabId = TAB.DETAILS;
	@observable leadStore: LeadStore = LeadStoreModel.create({});
	@observable doStageSelection = false;
	@observable abstractStage?: CaseStage;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.leadStore.load(this.props.params.leadId!);
		await this.leadStore.loadTransitions(this.leadStore.lead!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.leadId !== prevProps.params.leadId) {
			await this.leadStore.load(this.props.params.leadId!);
			await this.leadStore.loadTransitions(this.leadStore.lead!);
		}
	}

	render() {
		const lead = this.leadStore.lead!;
		if (session.isNetworkActive || !lead) {
			return <Spinner variant="brand" size="large" />;
		}
		return (
			<>
				<ItemHeader
					store={this.leadStore}
					details={this.getHeaderDetails(lead)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemPath
						store={this.leadStore}
						stageList={lead.meta!.caseStages!}
						currentStage={lead.caseStage!}
						handleStageTransition={this.handleStageTransition}
						onTransitionToStage={this.onTransitionToStage}
					/>
					<ItemLeftPart hasItemPath>
						<FormItemEditor
							store={this.leadStore}
							entityType={EntityType.LEAD}
							formId="lead/editLead"
							itemAlias={EntityType.LEAD}
							showEditButtons={this.activeLeftTabId === TAB.DETAILS}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
							key={"lead-" + this.leadStore.lead?.id}
						>
							{(editor) => (
								<Tabs
									className="full-height"
									selectedIndex={this.activeLeftTabId}
									onSelect={(tabId: any) => (this.activeLeftTabId = tabId)}
								>
									<TabsPanel label="Details">
										{this.activeLeftTabId === TAB.DETAILS && editor}
									</TabsPanel>
									{/* @ts-ignore */}
									<TabsPanel label="Account" disabled={!lead.account}>
										{lead.account && this.activeLeftTabId === TAB.ACCOUNT && (
											<div className="slds-m-horizontal_medium">
												<ItemDetailView
													formId="account/editAccount"
													itemAlias="account"
													itemSnapshot={lead.account.formSnapshot}
												/>
											</div>
										)}
									</TabsPanel>
								</Tabs>
							)}
						</FormItemEditor>
					</ItemLeftPart>
					<ItemRightPart store={this.leadStore} hasItemPath />
				</ItemGrid>
				{this.doStageSelection && (
					<StageSelector
						heading="Close this Lead"
						abstractTargetStage={this.abstractStage!}
						onStageSelection={(stage: CaseStage) => this.onTransitionToStage(stage)}
						onCancel={() => (this.doStageSelection = false)}
					/>
				)}
			</>
		);
	}

	private getHeaderDetails(lead: Lead): HeaderDetail[] {
		const details: HeaderDetail[] = [
			{
				label: "Owner",
				content: lead.owner!.caption,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={lead.owner!.picture}
						imgAlt={lead.owner!.caption}
						label={lead.owner!.caption}
					/>
				),
				link: "/user/" + lead.owner!.id
			}
		];
		if (lead.contact) {
			details.push({
				label: "Contact",
				content: lead.contact!.caption,
				icon: <Icon category="standard" name="contact" size="small" />,
				link: "/contact/" + lead.contact!.id
			});
		} else if (lead.account) {
			details.push({
				label: "Account",
				content: lead.account!.caption,
				icon: <Icon category="standard" name="account" size="small" />,
				link: "/account/" + lead.account!.id
			});
		} else {
			details.push({
				label: "Name",
				content: lead.fullName
			});
		}
		return details.concat([
			{ label: "Subject", content: lead.subject, truncate: true },
			{ label: "Source", content: lead.leadSource?.name },
			{ label: "Rating", content: lead.leadRating?.name }
		]);
	}

	private getHeaderActions() {
		return (
			<ButtonGroup variant="list">
			</ButtonGroup>
		);
	}

	private openEditor = () => {
		this.leadStore.edit();
	};

	private cancelEditor = async () => {
		await this.leadStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.leadStore.store();
			this.ctx.showToast("success", `Lead stored`);
			return item;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Lead: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

	private handleStageTransition = (stage: CaseStage) => {
		if (stage.isAbstract) {
			this.abstractStage = stage;
			this.doStageSelection = true;
		}
	};

	// private async convertLead(conversionInfo: ConversionInfo) {
	// 	try {
	// 		const result = await LEAD_API.convertLead(conversionInfo);
	// 		transaction(() => {
	// 			this.conversionResult = {
	// 				account: result.account[Object.keys(result.account)[0]],
	// 				contact: result.contact[Object.keys(result.contact)[0]],
	// 				opportunity: result.opportunity?.[Object.keys(result.opportunity)?.[0]],
	// 				advice: result.advice?.[Object.keys(result.advice)?.[0]]
	// 			};
	// 			this.doLeadConversion = false;
	// 			this.showLeadConversion = true;
	// 		});
	// 		this.ctx.showToast("success", `Lead converted`);
	// 	} catch (error: any) {
	// 		this.ctx.showAlert(
	// 			"error",
	// 			"Could not convert Lead: " + (error.detail ? error.detail : error.title ? error.title : error)
	// 		);
	// 	}
	// }

	// private async finishConversion() {
	// 	transaction(() => {
	// 		this.doLeadConversion = false;
	// 		this.showLeadConversion = false;
	// 	});
	// 	this.props.navigate("/lead");
	// }

	private onTransitionToStage = async (stage: CaseStage) => {
		try {
			const doc = (await this.leadStore.transitionTo(toJS(stage))) as Lead;
			this.doStageSelection = false;
			this.ctx.showToast("success", `Lead stored`);
			return doc;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Lead: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
			return Promise.reject(error);
		}
	};

}

export default withRouter(LeadPage);
