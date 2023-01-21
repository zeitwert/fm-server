
import { Avatar } from "@salesforce/design-system-react";
import { assertThis, DateFormat, session, TaskStore, TaskStoreModel } from "@zeitwert/ui-model";
import { StoreWithTasks } from "@zeitwert/ui-model/fm/collaboration/model/StoreWithTasks";
import { Task, TaskPayload } from "@zeitwert/ui-model/fm/collaboration/model/TaskModel";
import { computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React, { FC } from "react";
import ReactMarkdown from "react-markdown";
import MiniTaskForm from "./MiniTaskForm";

export interface TasksTabProps {
	relatedToId: string;
	store: StoreWithTasks;
	tasks: Task[];
	onTaskStored: () => void;
}

type TaskData = Omit<TaskPayload, "account">;

@observer
export default class TasksTab extends React.Component<TasksTabProps> {

	@observable taskStore = TaskStoreModel.create({});
	@observable isEditActive: boolean = false;
	@observable editTaskId: string | undefined;
	@computed get isNew() { return !this.editTaskId; }
	@computed get isNoEdit() { return !this.editTaskId && !this.isEditActive; }
	@computed get isEditNew() { return !this.editTaskId && this.isEditActive; }
	@computed get isEdit() { return !!this.editTaskId; }
	//   prepNewTask() => NoEdit: !editTaskId, !isEditActive
	//   startEditNew() => EditNew: !editTaskId, isEditActive
	//   cancelEditNew() => prepNewTask() [=> NoEdit]
	//   storeEditNew() => prepNewTask() [=> NoEdit]
	//   startModify() => Edit: !!editTaskId, isEditActive (after store.load())

	constructor(props: TasksTabProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount(): void {
		this.prepNewTask();
	}

	componentWillUnmount(): void {
		this.clearTask();
	}

	render() {
		const tasks = this.props.tasks;
		toJS(tasks); // necessary to trigger re-render after update :-(
		return (
			<div className="slds-is-relative">
				<div className="slds-m-left_small slds-m-right_small">
					<div className="slds-feed">
						<ul className="slds-feed__list">
							{
								(this.isNoEdit || this.isEditNew) &&
								<li className="slds-feed__item" key="task-add">
									<TaskEditor
										isNew={true}
										store={this.taskStore}
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
										isNew={false}
										store={this.taskStore}
										onStart={() => { }}
										onCancel={this.cancelModify}
										onOk={this.storeModify}
									/>
									<hr style={{ marginBlockStart: "12px", marginBlockEnd: 0 }} />
								</li>
							}
							{
								this.isNoEdit && !tasks.length &&
								<li className="slds-feed__item" key="task-0">
									<div>Keine Aufgaben</div>
									<hr style={{ marginBlockStart: "12px", marginBlockEnd: 0 }} />
								</li>
							}
							{
								this.isNoEdit && !!tasks.length &&
								tasks.map((task, index) => (
									<li className="slds-feed__item" key={"task-" + index}>
										<TaskView
											task={task}
											onEdit={(task) => { this.startModify(task.id) }}
											onChangePrivacy={this.changePrivacy}
											onRemove={(task) => this.removeTask(task.id)}
										/>
										<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
									</li>
								))
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

	private prepNewTask = () => {
		try {
			this.taskStore.create({
				owner: session.sessionInfo!.user,
				assignee: session.sessionInfo?.user,
				tenant: session.sessionInfo?.tenant,
				relatedTo: { id: this.props.relatedToId },
				isPrivate: false,
				priority: { id: "normal", name: "Normal" },
				dueAt: new Date(),
			});
			// if (!session.isKernelTenant) {
			// 	this.taskStore.task!.setAccount(session.sessionInfo?.account?.id);
			// }
			this.editTaskId = undefined;
			this.isEditActive = false;
		} catch (e: any) {
			console.error("prepNewTask crashed", e);
			throw e;
		}
	}

	private startEditNew = () => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(!this.isEditActive, "!isEditActive");
		try {
			this.isEditActive = true;
		} catch (e: any) {
			console.error("startEditNew crashed", e);
			throw e;
		}
	}

	private cancelEditNew = async () => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(this.isEditActive, "isEditActive");
		assertThis(this.taskStore.isInTrx, "store in trx");
		try {
			this.taskStore.cancel();
			this.taskStore.clear();
			this.prepNewTask();
		} catch (e: any) {
			console.error("cancelEditNew crashed", e);
			throw e;
		}
	}

	private storeEditNew = async () => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(this.isEditActive, "isEditActive");
		assertThis(this.taskStore.isInTrx, "store in trx");
		const task = this.taskStore.task!;
		try {
			await this.props.store.addTask(this.props.relatedToId, task);
			this.taskStore.commitTrx();
			this.taskStore.clear();
			this.prepNewTask();
			this.props.onTaskStored();
		} catch (e: any) {
			console.error("storeEditNew crashed", task, e);
			throw e;
		}
	}

	private startModify = async (taskId: string) => {
		assertThis(!this.editTaskId, "editTaskId is undefined");
		assertThis(!this.isEditActive, "!isEditActive");
		assertThis(this.taskStore.isInTrx, "store in trx");
		try {
			this.taskStore.cancel();
			this.taskStore.clear();
			await this.taskStore.load(taskId);
			this.taskStore.edit();
			this.editTaskId = taskId;
			this.isEditActive = true;
		} catch (e: any) {
			console.error("startModify crashed", taskId, e);
			throw e;
		}
	}

	private cancelModify = async () => {
		assertThis(!!this.editTaskId, "editTaskId is defined");
		assertThis(this.isEditActive, "isEditActive");
		assertThis(this.taskStore.isInTrx, "store in trx");
		try {
			this.taskStore.cancel();
			this.taskStore.clear();
			this.prepNewTask();
		} catch (e: any) {
			console.error("cancelModify crashed", e);
			throw e;
		}
	}

	private storeModify = async () => {
		assertThis(!!this.editTaskId, "editTaskId is defined");
		assertThis(this.isEditActive, "isEditActive");
		assertThis(this.taskStore.isInTrx, "store in trx");
		const task = this.taskStore.task!;
		try {
			await this.props.store.storeTask(task.id, task);
			this.taskStore.commitTrx();
			this.taskStore.clear();
			this.prepNewTask();
			this.props.onTaskStored();
		} catch (e: any) {
			console.error("cancelModify crashed", task, e);
			throw e;
		}
	}

	private clearTask = () => {
		try {
			if (this.taskStore.inTrx) {
				this.taskStore.cancel();
			}
			this.taskStore.clear();
		} catch (e: any) {
			console.error("clearTask crashed", e);
			throw e;
		}
	}

	private changePrivacy = (task: Task): void => {
		this.modifyTask(task.id, Object.assign({}, task, { isPrivate: !task.isPrivate }));
	}

	private modifyTask = async (id: string, task: TaskData) => {
		await this.props.store.storeTask(id, task);
		this.isEditActive = false;
		this.editTaskId = undefined;
	}

	private removeTask = async (id: string) => {
		await this.props.store.removeTask(id);
		this.isEditActive = true;
	}

}

interface TaskViewProps {
	task: Task;
	onEdit: (task: Task) => void;
	onChangePrivacy: (task: Task) => void;
	onRemove: (task: Task) => void;
}

@observer
class TaskView extends React.Component<TaskViewProps> {

	render() {

		const task = this.props.task;
		const isPrivate = task.isPrivate;
		const user = task.meta?.createdByUser!;
		const userName = user.name;
		const userAvatar = session.avatarUrl(user.id);
		const time = DateFormat.relativeTime(task.meta?.modifiedAt || task.meta?.createdAt!);

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
									<a href={`/user/${user.id}`} title={userName}>{userName}</a>
								</p>
							</div>
							<div className="slds-float_right">
								<TaskHeaderAction
									icon={isPrivate ? "lock" : "unlock"}
									label={isPrivate ? "Private Notiz" : "Öffentliche Notiz"}
									onClick={() => this.props.onChangePrivacy(task)}
								/>
								<TaskHeaderAction
									icon={"delete"}
									label={"Löschen"}
									onClick={() => this.props.onRemove(task)}
								/>
								<TaskHeaderAction
									icon={"edit"}
									label={"Bearbeiten"}
									onClick={() => this.props.onEdit(task)}
								/>
							</div>
						</div>
						<p className="slds-text-body_small">
							<a href="/#" title="..." className="slds-text-link_reset">{time}</a>
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
	isNew: boolean;
	store: TaskStore;
	onStart: () => void;
	onCancel: () => Promise<void>;
	onOk: () => Promise<void>;
}

@observer
class TaskEditor extends React.Component<TaskEditorProps> {

	render() {
		const { store } = this.props;
		if (!store.task?.id) {
			return null;
		}
		return <MiniTaskForm
			task={store.task}
			isNew={this.props.isNew}
			onStart={this.props.onStart}
			onCancel={this.props.onCancel}
			onOk={this.props.onOk}
		/>;
	}

}
