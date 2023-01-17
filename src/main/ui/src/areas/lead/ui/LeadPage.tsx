
import { Avatar, ButtonGroup, Icon, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { CaseStage, EntityType, EntityTypeInfo, EntityTypes, Lead, LeadStore, LeadStoreModel, session, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import { StageSelector } from "lib/doc/ui/StageSelector";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart } from "lib/item/ui/ItemPage";
import ItemPath from "lib/item/ui/ItemPath";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

enum LEFT_TABS {
	DETAILS = "static-data",
	ACCOUNT = "account",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class LeadPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.LEAD];

	@observable leadStore: LeadStore = LeadStoreModel.create({});
	@observable activeLeftTabId = LEFT_TABS.DETAILS;
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
		session.setHelpContext(`${EntityType.LEAD}-${this.activeLeftTabId}`);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.leadId !== prevProps.params.leadId) {
			await this.leadStore.load(this.props.params.leadId!);
			await this.leadStore.loadTransitions(this.leadStore.lead!);
		}
	}

	render() {

		const lead = this.leadStore.lead!;
		if (!lead && session.isNetworkActive) {
			return <></>;
		} else if (!lead) {
			return <NotFound entityType={this.entityType} id={this.props.params.leadId!} />;
		}

		const allowEdit = ([LEFT_TABS.DETAILS].indexOf(this.activeLeftTabId) >= 0);

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
						readOnly={this.leadStore.isInTrx}
						currentStage={lead.meta?.caseStage!}
						handleStageTransition={this.handleStageTransition}
						onTransitionToStage={this.onTransitionToStage}
					/>
					<ItemLeftPart>
						<ItemEditor
							store={this.leadStore}
							entityType={EntityType.LEAD}
							showEditButtons={allowEdit && !session.hasReadOnlyRole}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
						>
							<Tabs
								className="full-height"
								selectedIndex={LEFT_TAB_VALUES.indexOf(this.activeLeftTabId)}
								onSelect={(tabId: number) => (this.activeLeftTabId = LEFT_TAB_VALUES[tabId])}
							>
								<TabsPanel label="Details">
									<div>Well, that is inconvenient</div>
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
				</ItemGrid>
				{
					this.doStageSelection && (
						<StageSelector
							heading="Close this Lead"
							abstractTargetStage={this.abstractStage!}
							onStageSelection={(stage: CaseStage) => this.onTransitionToStage(stage)}
							onCancel={() => (this.doStageSelection = false)}
						/>
					)
				}
				{
					session.isNetworkActive &&
					<Spinner variant="brand" size="large" />
				}
			</>
		);
	}

	private getHeaderDetails(lead: Lead): HeaderDetail[] {
		const leadOwner: UserInfo = lead.owner as UserInfo;
		const details: HeaderDetail[] = [
			{
				label: "Owner",
				content: leadOwner.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(leadOwner.id)}
						imgAlt={leadOwner.name}
						label={leadOwner.name}
					/>
				),
				link: "/user/" + lead.owner!.id
			}
		];
		if (lead.contact) {
			details.push({
				label: "Contact",
				content: lead.contact.caption,
				icon: <Icon category="standard" name="contact" size="small" />,
				link: "/contact/" + lead.contact!.id
			});
		} else if (lead.account) {
			details.push({
				label: "Account",
				content: lead.account.caption,
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
		this.leadStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.leadStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.leadStore.load(this.props.params.leadId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
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
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return doc;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
			return Promise.reject(error);
		}
	};

}

export default withRouter(LeadPage);
