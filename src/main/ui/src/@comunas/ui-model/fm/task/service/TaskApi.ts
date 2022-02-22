import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { TaskSnapshot } from "../model/TaskModel";
import { TaskApiImpl } from "./impl/TaskApiImpl";

export interface TaskApi extends AggregateApi<TaskSnapshot> {
	findUpcomingTasks(size: number): Promise<TaskSnapshot[]>;
}

export const TASK_API: TaskApi = new TaskApiImpl();
