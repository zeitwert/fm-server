import { useQuery } from "@tanstack/react-query";
import { taskApi, taskListApi } from "./api";
import type { Task } from "./types";
import { useUpdateEntity } from "@/common/hooks";

export const taskKeys = {
	all: ["task"] as const,
	lists: () => [...taskKeys.all, "list"] as const,
	list: (params?: string) => [...taskKeys.lists(), params] as const,
	details: () => [...taskKeys.all, "detail"] as const,
	detail: (id: string) => [...taskKeys.details(), id] as const,
};

export function useTaskList() {
	return useQuery({
		queryKey: taskKeys.lists(),
		queryFn: () => taskListApi.list(),
	});
}

export function useTaskQuery(id: string) {
	return useQuery({
		queryKey: taskKeys.detail(id),
		queryFn: () => taskApi.get(id),
		enabled: !!id,
	});
}

export function useUpdateTask() {
	return useUpdateEntity<Task>({
		updateFn: taskApi.update,
		queryKey: taskKeys.details(),
		listQueryKey: taskKeys.lists(),
		successMessageKey: "task:message.saved",
	});
}

export function getTaskQueryOptions(id: string) {
	return {
		queryKey: taskKeys.detail(id),
		queryFn: () => taskApi.get(id),
	};
}

export function getTaskListQueryOptions() {
	return {
		queryKey: taskKeys.lists(),
		queryFn: () => taskListApi.list(),
	};
}
