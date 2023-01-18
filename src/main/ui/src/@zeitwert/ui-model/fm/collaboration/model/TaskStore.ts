
import { transaction } from "mobx";
import { cast, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app/common/service/JsonApi";
import { DocStoreModel } from "../../../ddd/doc/model/DocStore";
import { StoreWithAccountsModel } from "../../account/model/StoreWithAccounts";
import { TaskApi, TASK_API } from "../service/TaskApi";
import { Task, TaskModel, TaskModelType, TaskSnapshot } from "./TaskModel";

const MstTaskStoreModel = DocStoreModel.named("TaskStore")
	.props({
		accountsStore: types.optional(StoreWithAccountsModel, {}),
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
	.actions((self) => {
		const superAfterLoad = self.afterLoad;
		const afterLoad = (repository: EntityTypeRepository) => {
			superAfterLoad(repository);
			self.accountsStore.afterLoad(repository);
		}
		return { afterLoad };
	})
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
