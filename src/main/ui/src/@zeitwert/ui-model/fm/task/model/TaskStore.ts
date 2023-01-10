import { transaction } from "mobx";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { DocStoreModel } from "../../../ddd/doc/model/DocStore";
import { TaskApi, TASK_API } from "../service/TaskApi";
import { Task, TaskModel, TaskModelType, TaskSnapshot } from "./TaskModel";

const MstTaskStoreModel = DocStoreModel.named("TaskStore")
	.props({
		task: types.maybe(TaskModel)
	})
	.views((self) => ({
		get model(): TaskModelType {
			return TaskModel;
		},
		get api(): TaskApi {
			return TASK_API;
		},
		get item(): Task | undefined {
			return self.task;
		}
	}))
	.actions((self) => ({
		setItem(snapshot: TaskSnapshot) {
			transaction(() => {
				self.task = undefined;
				self.task = cast(snapshot);
			});
		}
	}));

type MstTaskStoreType = typeof MstTaskStoreModel;
interface MstTaskStore extends MstTaskStoreType { }

export const TaskStoreModel: MstTaskStore = MstTaskStoreModel;
export type TaskStoreModelType = typeof TaskStoreModel;
export interface TaskStore extends Instance<TaskStoreModelType> { }
export type TaskStoreSnapshot = SnapshotIn<TaskStoreModelType>;
export type TaskStorePayload = Omit<TaskStoreSnapshot, "id">;
