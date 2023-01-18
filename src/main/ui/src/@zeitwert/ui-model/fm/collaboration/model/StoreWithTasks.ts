
import { transaction } from "mobx";
import { applySnapshot, flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { TASK_API } from "../service/TaskApi";
import { Task, TaskModel, TaskPayload, TaskSnapshot } from "./TaskModel";

const MstStoreWithTasksModel = StoreWithEntitiesModel
	.named("StoreWithTasks")
	.props({
		tasks: types.optional(types.array(TaskModel), []),
	})
	.views((self) => ({
		getTask(id: string): Task | undefined {
			return self.tasks.find(n => n.id === id);
		},
	}))
	.actions((self) => ({
		async loadTasks(relatedToId: string): Promise<void> {
			return flow<void, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield TASK_API.getAggregates("filter[relatedToId]=" + relatedToId);
					const tasksRepo = repository["task"];
					if (tasksRepo) {
						transaction(() => {
							self.tasks.clear();
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
		async addTask(relatedToId: string, taskPayload: TaskPayload): Promise<Task> {
			const task: TaskSnapshot = Object.assign({}, taskPayload, { id: "New:" + new Date().getTime(), relatedToId: relatedToId });
			return flow<Task, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield TASK_API.createAggregate(task);
					const tasksRepo = repository["task"];
					if (tasksRepo) {
						transaction(() => {
							Object.keys(tasksRepo)
								.map((id) => tasksRepo[id])
								.forEach((snapshot) => self.tasks.unshift(snapshot));
						});
					}
				} catch (error: any) {
					console.error("Failed to add task", task, error);
					return Promise.reject(error);
				}
			})();
		},
		async storeTask(id: string, taskPayload: TaskPayload): Promise<Task> {
			const task = Object.assign(
				{},
				taskPayload,
				{
					id: id,
					meta: {
						clientVersion: self.getTask(id)?.meta?.version
					}
				}
			);
			return flow<Task, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield TASK_API.storeAggregate(task);
					const tasksRepo = repository["task"];
					if (tasksRepo) {
						let task: Task = self.getTask(id)!;
						applySnapshot(task, tasksRepo[id]);
					}
				} catch (error: any) {
					console.error("Failed to store task", task, error);
					return Promise.reject(error);
				}
			})();
		},
		async removeTask(id: string): Promise<Task> {
			return flow<Task, any[]>(function* (): any {
				try {
					const task = self.getTask(id);
					if (!!task) {
						const index = self.tasks.indexOf(task);
						yield TASK_API.deleteAggregate(id);
						self.tasks.splice(index, 1);
					}
				} catch (error: any) {
					console.error("Failed to remove task", id, error);
					return Promise.reject(error);
				}
			})();
		},
	}));

type MstStoreWithTasksType = typeof MstStoreWithTasksModel;
interface MstStoreWithTasks extends MstStoreWithTasksType { }

export const StoreWithTasksModel: MstStoreWithTasks = MstStoreWithTasksModel;
export type StoreWithTasksModelType = typeof StoreWithTasksModel;
export interface StoreWithTasks extends Instance<StoreWithTasksModelType> { }
export type StoreWithTasksSnapshot = SnapshotIn<StoreWithTasksModelType>;
export type StoreWithTasksPayload = Omit<StoreWithTasksSnapshot, "id">;
