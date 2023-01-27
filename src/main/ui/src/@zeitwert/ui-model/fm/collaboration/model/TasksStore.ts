
import { transaction } from "mobx";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { TASK_API } from "../service/TaskApi";
import { Task, TaskModel } from "./TaskModel";

const MstTasksStoreModel = StoreWithEntitiesModel
	.named("TasksStore")
	.props({
		tasks: types.optional(types.array(TaskModel), []),
	})
	.views((self) => ({
		getTask(id: string): Task | undefined {
			return self.tasks.find(n => n.id === id);
		},
		get futureTasks(): Task[] {
			const now = new Date();
			return self.tasks.filter(t => t.dueAt! > now && t.meta?.caseStage.id !== "task.done");
		},
		get overdueTasks(): Task[] {
			const now = new Date();
			return self.tasks.filter(t => t.dueAt! <= now && t.meta?.caseStage.id !== "task.done");
		},
		get completedTasks(): Task[] {
			return self.tasks.filter(t => t.meta?.caseStage.id === "task.done");
		},
	}))
	.actions((self) => ({
		async load(relatedToId: string): Promise<void> {
			return flow<void, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield TASK_API.getAggregates("filter[relatedToId]=" + relatedToId);
					self.tasks.clear();
					const tasksRepo = repository["task"];
					if (tasksRepo) {
						transaction(() => {
							Object.keys(tasksRepo)
								.map((id) => tasksRepo[id])
								.sort((a: Task, b: Task) => (a.dueAt! > b.dueAt! ? -1 : 1))
								.forEach((snapshot) => self.tasks.push(snapshot));
						});
					}
				} catch (error: any) {
					console.error("Failed to load tasks", error);
					return Promise.reject(error);
				}
			})();
		},
	}));

type MstTasksStoreType = typeof MstTasksStoreModel;
interface MstTasksStore extends MstTasksStoreType { }

export const TasksStoreModel: MstTasksStore = MstTasksStoreModel;
export type TasksStoreModelType = typeof TasksStoreModel;
export interface TasksStore extends Instance<TasksStoreModelType> { }
export type TasksStoreSnapshot = SnapshotIn<TasksStoreModelType>;
export type TasksStorePayload = Omit<TasksStoreSnapshot, "id">;
