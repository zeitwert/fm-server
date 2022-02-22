import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { DocStoreModel } from "../../../ddd/doc/model/DocStore";
import { TASK_API } from "../service/TaskApi";
import { Task, TaskModel } from "./TaskModel";

const MstTaskStoreModel = DocStoreModel.named("TaskStore")
	.props({
		task: types.maybe(TaskModel)
	})
	.views((self) => ({
		get model() {
			return TaskModel;
		},
		get api() {
			return TASK_API;
		},
		get item(): Task | undefined {
			return self.task;
		}
	}))
	.actions((self) => ({
		setItem(item: Task) {
			self.task = item;
		}
	}));

type MstTaskStoreType = typeof MstTaskStoreModel;
export interface MstTaskStore extends MstTaskStoreType { }
export const TaskStoreModel: MstTaskStore = MstTaskStoreModel;
export interface TaskStore extends Instance<typeof TaskStoreModel> { }
export type MstTaskStoreSnapshot = SnapshotIn<typeof MstTaskStoreModel>;
export interface TaskStoreSnapshot extends MstTaskStoreSnapshot { }
