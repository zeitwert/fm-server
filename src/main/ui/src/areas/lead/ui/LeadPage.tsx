
import { Avatar, ButtonGroup, Icon, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { CaseStage, EntityType, EntityTypeInfo, EntityTypes, Lead, LeadStore, LeadStoreModel, NotesStore, NotesStoreModel, session, TasksStore, TasksStoreModel, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import { StageSelector } from "lib/doc/ui/StageSelector";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "lib/item/ui/ItemPage";
import ItemPath from "lib/item/ui/ItemPath";
import DocStageHistoryTab from "lib/item/ui/tab/DocStageHistoryTab";
import NotesTab from "lib/item/ui/tab/NotesTab";
import TasksTab from "lib/item/ui/tab/TasksTab";
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

enum LEFT_TABS {
	MAIN = "main",
	ACCOUNT = "account",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	NOTES = "notes",
	TASKS = "tasks",
	ACTIVITIES = "activities",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("showAlert", "showToast")
@observer
class LeadPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.LEAD];

	@observable leadStore: LeadStore = LeadStoreModel.create({});
	@observable notesStore: NotesStore = NotesStoreModel.create({});
	@observable tasksStore: TasksStore = TasksStoreModel.create({});

	@observable activeLeftTabId = LEFT_TABS.MAIN;
	@observable activeRightTabId = RIGHT_TABS.NOTES;
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
		session.setHelpContext(`${EntityType.LEAD}-${this.activeLeftTabId}`);
		await this.leadStore.load(this.props.params.leadId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.leadId !== prevProps.params.leadId) {
			await this.leadStore.load(this.props.params.leadId!);
		}
	}

	render() {

		const lead = this.leadStore.lead!;
		if (!lead && session.isNetworkActive) {
			return <></>;
		} else if (!lead) {
			return <NotFound entityType={this.entityType} id={this.props.params.leadId!} />;
		}

		const allowEdit = ([LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.notesStore.notes.length;
		const tasksCount = this.tasksStore.futureTasks.length + this.tasksStore.overdueTasks.length;

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
					<ItemRightPart>
						<Tabs
							className="full-height"
							selectedIndex={RIGHT_TAB_VALUES.indexOf(this.activeRightTabId)}
							onSelect={(tabId: number) => (this.activeRightTabId = RIGHT_TAB_VALUES[tabId])}
						>
							<TabsPanel label={"Notizen" + (notesCount ? ` (${notesCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.NOTES &&
									<NotesTab relatedToId={this.leadStore.id!} notesStore={this.notesStore} />
								}
							</TabsPanel>
							<TabsPanel label={"Aufgaben" + (tasksCount ? ` (${tasksCount})` : "")}>
								{
									this.activeRightTabId === RIGHT_TABS.TASKS &&
									<TasksTab relatedToId={this.leadStore.id!} tasksStore={this.tasksStore} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<DocStageHistoryTab doc={lead} />
								}
							</TabsPanel>
							{
								lead.hasValidations &&
								<TabsPanel label={`Validierungen (${lead.validationsCount})`}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={lead.meta?.validationList!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
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
