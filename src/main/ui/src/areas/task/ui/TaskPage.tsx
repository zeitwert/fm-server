import { Avatar, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { CaseStage, EntityType, EntityTypeInfo, EntityTypes, session, Task, TaskStore, TaskStoreModel, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import NotFound from "app/ui/NotFound";
import { StageSelector } from "lib/doc/ui/StageSelector";
import ItemEditor from "lib/item/ui/ItemEditor";
import ItemHeader, { HeaderDetail } from "lib/item/ui/ItemHeader";
import { ItemGrid, ItemLeftPart } from "lib/item/ui/ItemPage";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import TaskStaticDataForm from "./tabs/TaskStaticDataForm";

enum LEFT_TABS {
	DETAILS = "static-data",
}
const LEFT_TAB_VALUES = Object.values(LEFT_TABS);

@inject("appStore", "session", "showAlert", "showToast")
@observer
class TaskPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.TASK];

	@observable taskStore: TaskStore = TaskStoreModel.create({});
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
		await this.taskStore.load(this.props.params.taskId!);
		await this.taskStore.loadTransitions(this.taskStore.task!);
		session.setHelpContext(`${EntityType.TASK}-${this.activeLeftTabId}`);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.taskId !== prevProps.params.taskId) {
			await this.taskStore.load(this.props.params.taskId!);
			await this.taskStore.loadTransitions(this.taskStore.task!);
		}
	}

	render() {

		const task = this.taskStore.task!;
		if (!task && session.isNetworkActive) {
			return <></>;
		} else if (!task) {
			return <NotFound entityType={this.entityType} id={this.props.params.taskId!} />;
		}

		const allowEdit = ([LEFT_TABS.DETAILS].indexOf(this.activeLeftTabId) >= 0);

		return (
			<>
				<ItemHeader
					store={this.taskStore}
					details={this.getHeaderDetails(task)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					{
						/*
						<ItemPath
							store={this.taskStore}
							stageList={task.meta!.caseStages!}
							currentStage={task.meta?.caseStage!}
							handleStageTransition={this.handleStageTransition}
							onTransitionToStage={this.onTransitionToStage}
						/>
						*/
					}
					<ItemLeftPart hasItemPath>
						<ItemEditor
							store={this.taskStore}
							entityType={EntityType.TASK}
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
									{this.activeLeftTabId === LEFT_TABS.DETAILS && <TaskStaticDataForm store={this.taskStore} />}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
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
		console.log("task.owner", taskOwner);
		const taskAssignee: UserInfo = task.meta?.assignee as UserInfo;
		console.log("task.assignee", taskAssignee, task.meta);
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

	private openEditor = () => {
		this.taskStore.edit();
	};

	private cancelEditor = async () => {
		this.taskStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.taskStore.store();
			this.ctx.showToast("success", `${this.entityType.labelSingular} gespeichert`);
			return item;
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await this.taskStore.load(this.props.params.taskId!);
			}
			this.ctx.showAlert(
				"error",
				(error.title ? error.title : `Konnte ${this.entityType.labelSingular} nicht speichern`) + ": " + (error.detail ? error.detail : error)
			);
		}
	};

	// private handleStageTransition = (stage: CaseStage) => {
	// 	if (stage.isAbstract) {
	// 		this.abstractStage = stage;
	// 		this.doStageSelection = true;
	// 	}
	// };

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
