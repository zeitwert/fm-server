import { useQuery } from "@tanstack/react-query";
import { taskApi, taskListApi } from "./api";

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

export function useTask(id: string) {
	return useQuery({
		queryKey: taskKeys.detail(id),
		queryFn: () => taskApi.get(id),
		enabled: !!id,
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
