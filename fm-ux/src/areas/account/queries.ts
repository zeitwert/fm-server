import { useQuery } from "@tanstack/react-query";
import { accountApi, accountListApi } from "./api";
import type { Account } from "./types";
import { useCreateEntity, useDeleteEntity } from "../../common/hooks";

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

export function useAccount(id: string) {
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
		successMessage: "Kunde erstellt",
	});
}

export function useDeleteAccount() {
	return useDeleteEntity({
		deleteFn: accountApi.delete,
		listQueryKey: accountKeys.lists(),
		successMessage: "Kunde gelöscht",
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
