
import { Avatar, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { CaseStage, EntityType, EntityTypeInfo, EntityTypes, NotesStore, NotesStoreModel, session, Task, TaskStore, TaskStoreModel, UserInfo } from "@zeitwert/ui-model";
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
import ValidationsTab from "lib/item/ui/tab/ValidationsTab";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import TaskMainForm from "./tabs/TaskMainForm";

enum LEFT_TABS {
	MAIN = "main",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

enum RIGHT_TABS {
	NOTES = "notes",
	ACTIVITIES = "activities",
	VALIDATIONS = "validations",
}
const RIGHT_TAB_VALUES = Object.values(RIGHT_TABS);

@inject("showAlert", "showToast")
@observer
class TaskPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.TASK];

	@observable taskStore: TaskStore = TaskStoreModel.create({});
	@observable notesStore: NotesStore = NotesStoreModel.create({});

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
		session.setHelpContext(`${EntityType.TASK}-${this.activeLeftTabId}`);
		await this.taskStore.load(this.props.params.taskId!);
		await this.notesStore.load(this.props.params.taskId!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.taskId !== prevProps.params.taskId) {
			await this.taskStore.load(this.props.params.taskId!);
			await this.notesStore.load(this.props.params.taskId!);
		}
	}

	render() {

		const task = this.taskStore.task!;
		if (!task && session.isNetworkActive) {
			return <></>;
		} else if (!task) {
			return <NotFound entityType={this.entityType} id={this.props.params.taskId!} />;
		}

		const allowEdit = ([LEFT_TABS.MAIN].indexOf(this.activeLeftTabId) >= 0);

		const notesCount = this.notesStore.notes.length;

		return (
			<>
				<ItemHeader
					store={this.taskStore}
					details={this.getHeaderDetails(task)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemPath
						store={this.taskStore}
						stageList={task.meta!.caseStages!}
						readOnly={this.taskStore.isInTrx}
						currentStage={task.meta?.caseStage!}
						handleStageTransition={this.handleStageTransition}
						onTransitionToStage={this.onTransitionToStage}
					/>
					<ItemLeftPart hasItemPath>
						<ItemEditor
							store={this.taskStore}
							entityType={EntityType.TASK}
							showEditButtons={allowEdit && !session.hasReadOnlyRole}
						>
							<Tabs
								className="full-height"
								selectedIndex={LEFT_TAB_VALUES.indexOf(this.activeLeftTabId)}
								onSelect={(tabId: number) => (this.activeLeftTabId = LEFT_TAB_VALUES[tabId])}
							>
								<TabsPanel label="Details">
									{this.activeLeftTabId === LEFT_TABS.MAIN && <TaskMainForm task={this.taskStore.task!} doEdit={this.taskStore.isInTrx} />}
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
									<NotesTab relatedToId={this.taskStore.id!} notesStore={this.notesStore} />
								}
							</TabsPanel>
							<TabsPanel label="Aktivität">
								{
									this.activeRightTabId === RIGHT_TABS.ACTIVITIES &&
									<DocStageHistoryTab doc={task} />
								}
							</TabsPanel>
							{
								task.hasValidations &&
								<TabsPanel label={`Validierungen (${task.validationsCount})`}>
									{
										this.activeRightTabId === RIGHT_TABS.VALIDATIONS &&
										<ValidationsTab validationList={task.meta?.validations!} />
									}
								</TabsPanel>
							}
						</Tabs>
					</ItemRightPart>
				</ItemGrid>
				{
					this.doStageSelection && (
						<StageSelector
							heading="Close this Task"
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

	private getHeaderDetails(task: Task): HeaderDetail[] {
		const taskOwner: UserInfo = task.owner as UserInfo;
		const taskAssignee: UserInfo = task.meta?.assignee as UserInfo;
		return [
			{
				label: "Assignee",
				content: taskAssignee?.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(taskAssignee?.id)}
						imgAlt={taskAssignee?.name}
						label={taskAssignee?.name}
					/>
				),
				link: "/user/" + taskAssignee?.id
			},
			{
				label: "Owner",
				content: taskOwner.name,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={session.avatarUrl(taskOwner.id)}
						imgAlt={taskOwner.name}
						label={taskOwner.name}
					/>
				),
				link: "/user/" + taskOwner!.id
			},
		];
	}

	private getHeaderActions() {
		return (<></>);
	}

	private handleStageTransition = (stage: CaseStage) => {
		if (stage.isAbstract) {
			this.abstractStage = stage;
			this.doStageSelection = true;
		}
	};

	private onTransitionToStage = async (stage: CaseStage) => {
		try {
			const doc = (await this.taskStore.transitionTo(toJS(stage))) as Task;
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

export default withRouter(TaskPage);
