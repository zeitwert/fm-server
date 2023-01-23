
import { Avatar, ExpandableSection } from "@salesforce/design-system-react";
import { assertThis, DateFormat, EntityType, EntityTypes, Enumerated, session, Task, TaskPayload, TaskSnapshot, TasksStore, TaskStoreModel, TASK_API } from "@zeitwert/ui-model";
import NotFound from "app/ui/NotFound";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React, { FC } from "react";
import ReactMarkdown from "react-markdown";
import MiniTaskForm from "./MiniTaskForm";

export interface TasksTabProps {
	relatedToId: string;
	tasksStore: TasksStore;
}

@observer
export default class TasksTab extends React.Component<TasksTabProps> {

	@observable editTaskId: string | undefined;
	@observable isEditActive: boolean = false;
	@observable showCompleted: boolean = false;

	@computed get isNew() { return !this.editTaskId; }
	@computed get isEdit() { return !!this.editTaskId; }
	@computed get isNoEdit() { return !this.editTaskId && !this.isEditActive; }
	@computed get isEditNew() { return !this.editTaskId && this.isEditActive; }

	constructor(props: TasksTabProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.loadTasks();
	}

	async componentDidUpdate(prevProps: TasksTabProps) {
		if (this.props.relatedToId !== prevProps.relatedToId) {
			this.loadTasks();
		}
	}

	render() {
		const tasks = this.props.tasksStore.tasks;
		return (
			<div className="slds-is-relative">
				<div className="slds-m-left_small slds-m-right_small">
					<div className="slds-feed">
						<ul className="slds-feed__list">
							{
								this.isNew &&
								<li className="slds-feed__item" key="task-add">
									<TaskEditor
										relatedToId={this.props.relatedToId}
										onStart={this.startEditNew}
										onCancel={this.cancelEditNew}
										onOk={this.storeEditNew}
									/>
									<hr style={{ marginBlockStart: "12px", marginBlockEnd: 0 }} />
								</li>
							}
							{
								this.isEdit &&
								<li className="slds-feed__item" key="task-edit">
									<TaskEditor
										relatedToId={this.props.relatedToId}
										taskId={this.editTaskId}
										onStart={() => { }}
										onCancel={this.cancelModify}
										onOk={this.storeModify}
									/>
									<hr style={{ marginBlockStart: "12px", marginBlockEnd: 0 }} />
								</li>
							}
							{
								this.isNoEdit &&
								<>
									{
										!tasks.length &&
										<li className="slds-feed__item" key="task-0">
											<div>Keine Aufgaben</div>
											<hr style={{ marginBlockStart: "12px", marginBlockEnd: 0 }} />
										</li>
									}
									{
										!!this.props.tasksStore.futureTasks.length &&
										this.props.tasksStore.futureTasks.map((task, index) => (
											<li className="slds-feed__item" key={"task-" + index}>
												<TaskView
													task={task}
													onEdit={(task) => { this.startModify(task.id) }}
													onChangePrivacy={this.changePrivacy}
													onComplete={this.completeTask}
												/>
												<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
											</li>
										))
									}
									{
										!!this.props.tasksStore.overdueTasks.length &&
										<ExpandableSection title="Überfällig">
											{
												this.props.tasksStore.overdueTasks.map((task, index) => (
													<li className="slds-feed__item" key={"task-" + index}>
														<TaskView
															task={task}
															onEdit={(task) => { this.startModify(task.id) }}
															onChangePrivacy={this.changePrivacy}
															onComplete={this.completeTask}
														/>
														<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
													</li>
												))
											}
										</ExpandableSection>
									}
									{
										!!this.props.tasksStore.completedTasks.length &&
										<ExpandableSection
											title={`Abgeschlossen (${this.props.tasksStore.completedTasks.length})`}
											isOpen={this.showCompleted}
											onToggleOpen={() => { this.showCompleted = !this.showCompleted; }}
										>
											{
												this.props.tasksStore.completedTasks.map((task, index) => (
													<li className="slds-feed__item" key={"task-" + index}>
														<TaskView task={task} />
														<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
													</li>
												))
											}
										</ExpandableSection>
									}
								</>
							}
						</ul>
					</div>
				</div>
			</div>
		);
	}

	// invariants:
	//   !editTaskId & !isEditActive => taskStore.inTrx with new task
	//   !editTaskId & isEditActive => taskStore.inTrx with new task
	//   !!editTaskId => isEditActive, taskStore.inTrx with existing task
	//   isNew = !editTaskId
	//
	// lifecycle:
	//   prepNewTask() => NoEdit: !editTaskId, !isEditActive
	//   startEditNew() => EditNew: !editTaskId, isEditActive
	//   cancelEditNew() => prepNewTask() [=> NoEdit]
	//   storeEditNew() => prepNewTask() [=> NoEdit]
	//   startModify() => Edit: !!editTaskId, isEditActive (after store.load())
	//   cancelModify() => prepNewTask() [=> NoEdit]
	//   storeModify() => prepNewTask() [=> NoEdit]
	//   clearTask() => ...

	private startEditNew = () => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(!this.isEditActive, "!isEditActive");
		this.isEditActive = true;
	}

	private cancelEditNew = async () => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(this.isEditActive, "isEditActive");
		this.isEditActive = false;
	}

	private storeEditNew = async () => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(this.isEditActive, "isEditActive");
		this.loadTasks();
	}

	private startModify = async (taskId: string) => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(!this.isEditActive, "!isEditActive");
		this.editTaskId = taskId;
		this.isEditActive = true;
	}

	private cancelModify = async () => {
		assertThis(!!this.editTaskId, "editTaskId is defined");
		assertThis(this.isEditActive, "isEditActive");
		this.editTaskId = undefined;
		this.isEditActive = false;
	}

	private storeModify = async () => {
		assertThis(!!this.editTaskId, "editTaskId is defined");
		assertThis(this.isEditActive, "isEditActive");
		this.loadTasks();
	}

	private changePrivacy = (task: Task): void => {
		this.modifyTask(task.id, Object.assign({}, task, { isPrivate: !task.isPrivate }));
	}

	private completeTask = async (task: Task) => {
		this.modifyTask(task.id, Object.assign({}, task, { nextCaseStage: { id: "task.done" } }));
	}

	private modifyTask = async (id: string, task: TaskPayload) => {
		const ts = Object.assign(
			{},
			task,
			{
				id: id,
				meta: {
					clientVersion: this.props.tasksStore.getTask(id)?.meta?.version
				}
			}
		);
		await TASK_API.storeAggregate(ts as TaskSnapshot);
		await this.loadTasks();
	}

	private loadTasks = async () => {
		this.editTaskId = undefined;
		this.isEditActive = false;
		await this.props.tasksStore.load(this.props.relatedToId);
	}

}

interface TaskViewProps {
	task: Task;
	onEdit?: (task: Task) => void;
	onChangePrivacy?: (task: Task) => void;
	onComplete?: (task: Task) => void;
}

@observer
class TaskView extends React.Component<TaskViewProps> {

	render() {

		const task = this.props.task;
		const isPrivate = task.isPrivate;
		const user = task.meta?.assignee!;
		const userName = user.name;
		const userAvatar = session.avatarUrl(user.id);
		const dueAt = DateFormat.compact(task.dueAt, false);
		const dueAtRelative = DateFormat.relativeTime(task.dueAt!);

		return (
			<article className="slds-post">
				<header className="slds-post__header slds-media">
					<div className="slds-media__figure">
						<Avatar
							variant="user"
							size="medium"
							imgSrc={userAvatar}
							label={userName}
						/>
					</div>
					<div className="slds-media__body">
						<div className="slds-clearfix xslds-grid xslds-grid_align-spread xslds-has-flexi-truncate">
							<div className="slds-float_left">
								<p>
									<strong><a href={`/task/${task.id}`}>{task.meta?.caseStage.name}</a> ⋅ </strong><a href={`/user/${user.id}`} title={userName}>{this.getUserName(user)}</a>
								</p>
							</div>
							<div className="slds-float_right">
								{
									!!this.props.onChangePrivacy &&
									<TaskHeaderAction
										icon={isPrivate ? "lock" : "unlock"}
										label={isPrivate ? "Private Aufgabe" : "Öffentliche Aufgabe"}
										onClick={() => this.props.onChangePrivacy?.(task)}
									/>
								}
								{
									!!this.props.onComplete &&
									<TaskHeaderAction
										icon={"success"}
										label={"Als erledigt markieren"}
										onClick={() => this.props.onComplete?.(task)}
									/>
								}
								{
									!!this.props.onEdit &&
									<TaskHeaderAction
										icon={"edit"}
										label={"Bearbeiten"}
										onClick={() => this.props.onEdit?.(task)}
									/>
								}
							</div>
						</div>
						<p className="slds-text-body_small">
							{dueAt} ⋅ {dueAtRelative}
						</p>
					</div>
				</header>
				<div className="slds-post__content xslds-text-longform">
					<div><strong>{task.subject || "(kein Titel)"}</strong></div>
					<ReactMarkdown className="fa-task-content">
						{task.content || "(kein Inhalt)"}
					</ReactMarkdown>
				</div>
			</article>
		);
	}

	private getUserName(user: Enumerated) {
		return user.id == session.sessionInfo?.user.id ? "Du" : user.name;
	}

}

interface TaskHeaderActionProps {
	icon: string;
	label: string;
	onClick?: () => void;
}

const TaskHeaderAction: FC<TaskHeaderActionProps> = (props) => {
	const { icon, label } = props;
	return (
		<span className="slds-m-left_small">
			<button className="slds-button slds-button_icon slds-button_icon-x-small" title={label} onClick={() => props.onClick?.()}>
				<svg className="slds-icon slds-icon-text-default slds-icon_x-small slds-align-middle">
					<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + icon}></use>
				</svg>
			</button>
		</span>
	);

};

interface TaskEditorProps {
	relatedToId: string;
	taskId?: string;
	onStart: () => void;
	onCancel: () => Promise<void>;
	onOk: () => Promise<void>;
}

@observer
class TaskEditor extends React.Component<TaskEditorProps> {

	@observable taskStore = TaskStoreModel.create({});

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.onStart();
	}

	async componentDidUpdate(prevProps: TaskEditorProps) {
		if ((this.props.taskId ?? "none") !== (prevProps.taskId ?? "none")) {
			this.onStart();
		}
	}

	render() {
		const task = this.taskStore.task!;
		if (!task && session.isNetworkActive) {
			return <></>;
		} else if (!task) {
			return <NotFound entityType={EntityTypes[EntityType.TASK]} id={this.props.taskId!} />;
		}
		return <MiniTaskForm
			task={task}
			isNew={!this.props.taskId}
			onStart={this.props.onStart}
			onCancel={this.onCancel}
			onOk={this.onOk}
		/>;
	}

	private onStart = async () => {
		const tomorrow = new Date();
		tomorrow.setDate(tomorrow.getDate() + 1);
		tomorrow.setHours(0, 0, 0, 0);
		if (this.props.taskId) {
			await this.taskStore.load(this.props.taskId);
			this.taskStore.edit();
		} else {
			this.taskStore.clear();
			this.taskStore.create({
				owner: session.sessionInfo!.user,
				assignee: session.sessionInfo?.user,
				tenant: session.sessionInfo?.tenant,
				relatedTo: { id: this.props.relatedToId },
				isPrivate: false,
				priority: { id: "normal", name: "Normal" },
				dueAt: tomorrow,
			});
		}
	}

	private onCancel = async () => {
		this.taskStore.cancel();
		await this.onStart();
		await this.props.onCancel();
	}

	private onOk = async () => {
		await this.taskStore.store();
		await this.onStart();
		await this.props.onOk();
	}

}
