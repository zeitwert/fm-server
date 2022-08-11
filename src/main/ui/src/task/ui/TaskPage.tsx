import { Avatar, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { CaseStage, EntityType, EntityTypeInfo, EntityTypes, session, Task, TaskStore, TaskStoreModel, UserInfo } from "@zeitwert/ui-model";
import { StageSelector } from "doc/ui/StageSelector";
import { AppCtx } from "frame/App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import NotFound from "frame/ui/NotFound";
import FormItemEditor from "item/ui/FormItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import ItemPath from "item/ui/ItemPath";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

enum LEFT_TABS {
	DETAILS = "static-data",
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class TaskPage extends React.Component<RouteComponentProps> {

	entityType: EntityTypeInfo = EntityTypes[EntityType.TASK];

	@observable activeLeftTabId = LEFT_TABS.DETAILS;
	@observable taskStore: TaskStore = TaskStoreModel.create({});
	@observable doStageSelection = false;
	@observable abstractStage?: CaseStage;

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	async componentDidMount() {
		await this.taskStore.load(this.props.params.taskId!);
		await this.taskStore.loadTransitions(this.taskStore.task!);
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.taskId !== prevProps.params.taskId) {
			await this.taskStore.load(this.props.params.taskId!);
			await this.taskStore.loadTransitions(this.taskStore.task!);
		}
	}

	render() {
		const task = this.taskStore.task!;
		if (session.isNetworkActive) {
			return <Spinner variant="brand" size="large" />;
		} else if (!task) {
			return <NotFound entityType={this.entityType} id={this.props.params.taskId!} />;
		}
		session.setHelpContext(`${EntityType.TASK}-${this.activeLeftTabId}`);

		return (
			<>
				<ItemHeader
					store={this.taskStore}
					details={this.getHeaderDetails(task)}
				/>
				<ItemGrid>
					<ItemPath
						store={this.taskStore}
						stageList={task.meta!.caseStages!}
						currentStage={task.caseStage!}
						handleStageTransition={this.handleStageTransition}
						onTransitionToStage={this.onTransitionToStage}
					/>
					<ItemLeftPart hasItemPath>
						<FormItemEditor
							store={this.taskStore}
							entityType={EntityType.TASK}
							formId="task/editTask"
							itemAlias={EntityType.TASK}
							control={{
								reminderSet: !!this.taskStore.task!.reminderDate
							}}
							onChange={async (path) => {
								if (path === "control.reminderSet") {
									await this.taskStore.task!.setField("reminderDate", undefined);
								}
							}}
							showEditButtons={this.activeLeftTabId === LEFT_TABS.DETAILS}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
							key={"task-" + this.taskStore.task?.id}
						>
							{(editor) => (
								<Tabs
									className="full-height"
									selectedIndex={this.activeLeftTabId}
									onSelect={(tabId: number) => (this.activeLeftTabId = Object.values(LEFT_TABS)[tabId])}
								>
									<TabsPanel label="Details">
										{this.activeLeftTabId === LEFT_TABS.DETAILS && editor}
									</TabsPanel>
								</Tabs>
							)}
						</FormItemEditor>
					</ItemLeftPart>
					<ItemRightPart store={this.taskStore} hideTask hasItemPath />
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
			</>
		);
	}

	private getHeaderDetails(task: Task): HeaderDetail[] {
		//const refItem = task.refDoc ? task.refDoc : task.refObj;
		const taskOwner: UserInfo = task.owner as UserInfo;
		return [
			{
				label: "Owner",
				content: taskOwner.caption,
				icon: (
					<Avatar
						variant="user"
						size="small"
						imgSrc={taskOwner.picture}
						imgAlt={taskOwner.caption}
						label={taskOwner.caption}
					/>
				),
				link: "/user/" + task.owner!.id
			},
			// {
			// 	label: "Reference",
			// 	content: refItem.caption,
			// 	icon: <Icon category={refItem.type.iconCategory} name={refItem.type.iconName} size="small" />,
			// 	link: "/" + (refItem.isDoc ? "doc" : "obj") + "/" + refItem.id
			// }
		];
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
			this.ctx.showToast("success", `Task stored`);
			return doc;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Task: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
			return Promise.reject(error);
		}
	};

}

export default withRouter(TaskPage);
