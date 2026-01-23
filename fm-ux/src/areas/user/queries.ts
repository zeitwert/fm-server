import { useQuery } from "@tanstack/react-query";
import { userApi, userListApi } from "./api";
import type { User } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";

export const userKeys = {
	all: ["user"] as const,
	lists: () => [...userKeys.all, "list"] as const,
	list: (params?: string) => [...userKeys.lists(), params] as const,
	details: () => [...userKeys.all, "detail"] as const,
	detail: (id: string) => [...userKeys.details(), id] as const,
};

export function useUserList() {
	return useQuery({
		queryKey: userKeys.lists(),
		queryFn: () => userListApi.list(),
	});
}

export function useUserQuery(id: string) {
	return useQuery({
		queryKey: userKeys.detail(id),
		queryFn: () => userApi.get(id),
		enabled: !!id,
	});
}

export function useCreateUser() {
	return useCreateEntity<User>({
		createFn: (data) => userApi.create(data),
		listQueryKey: userKeys.lists(),
		successMessageKey: "user:message.created",
	});
}

export function useUpdateUser() {
	return useUpdateEntity<User>({
		updateFn: userApi.update,
		queryKey: userKeys.details(),
		listQueryKey: userKeys.lists(),
		successMessageKey: "user:message.saved",
	});
}

export function useDeleteUser() {
	return useDeleteEntity({
		deleteFn: userApi.delete,
		listQueryKey: userKeys.lists(),
		successMessageKey: "user:message.deleted",
	});
}

export function getUserQueryOptions(id: string) {
	return {
		queryKey: userKeys.detail(id),
		queryFn: () => userApi.get(id),
	};
}

export function getUserListQueryOptions() {
	return {
		queryKey: userKeys.lists(),
		queryFn: () => userListApi.list(),
	};
}
