import { useQuery } from "@tanstack/react-query";
import { accountApi, accountListApi } from "./api";
import type { Account } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";

export const accountKeys = {
	all: ["account"] as const,
	lists: () => [...accountKeys.all, "list"] as const,
	list: (params?: string) => [...accountKeys.lists(), params] as const,
	details: () => [...accountKeys.all, "detail"] as const,
	detail: (id: string) => [...accountKeys.details(), id] as const,
};

export function useAccountList() {
	return useQuery({
		queryKey: accountKeys.lists(),
		queryFn: () => accountListApi.list(),
	});
}

export function useAccountQuery(id: string) {
	return useQuery({
		queryKey: accountKeys.detail(id),
		queryFn: () => accountApi.get(id),
		enabled: !!id,
	});
}

export function useCreateAccount() {
	return useCreateEntity<Account>({
		createFn: (data) => accountApi.create(data),
		listQueryKey: accountKeys.lists(),
		successMessageKey: "account:message.created",
	});
}

export function useUpdateAccount() {
	return useUpdateEntity<Account>({
		updateFn: accountApi.update,
		queryKey: accountKeys.details(),
		listQueryKey: accountKeys.lists(),
		successMessageKey: "account:message.saved",
	});
}

export function useDeleteAccount() {
	return useDeleteEntity({
		deleteFn: accountApi.delete,
		listQueryKey: accountKeys.lists(),
		successMessageKey: "account:message.deleted",
	});
}

export function getAccountQueryOptions(id: string) {
	return {
		queryKey: accountKeys.detail(id),
		queryFn: () => accountApi.get(id),
	};
}

export function getAccountListQueryOptions() {
	return {
		queryKey: accountKeys.lists(),
		queryFn: () => accountListApi.list(),
	};
}
