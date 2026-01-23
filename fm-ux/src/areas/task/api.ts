import { createEntityApi } from "../../common/api/entityApi";
import type { Task, TaskListItem } from "./types";

export const taskApi = createEntityApi<Task>({
	module: "collaboration",
	path: "tasks",
	type: "task",
	includes: "",
	relations: {},
});

export const taskListApi = createEntityApi<TaskListItem>({
	module: "collaboration",
	path: "tasks",
	type: "task",
	includes: "",
	relations: {},
});
