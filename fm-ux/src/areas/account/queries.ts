import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { accountApi, accountListApi } from "./api";
import type { Account } from "./types";
import type { EntityMeta } from "../../common/api/jsonapi";

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
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (data: Omit<Account, "id" | "tenant">) =>
			accountApi.create(data as Omit<Account, "id">),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: accountKeys.lists() });
			message.success("Kunde erstellt");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Erstellen: ${error.message}`);
		},
	});
}

export function useUpdateAccount() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (data: Partial<Account> & { id: string; meta?: EntityMeta }) =>
			accountApi.update(data),
		onSuccess: (_, variables) => {
			queryClient.invalidateQueries({ queryKey: accountKeys.detail(variables.id) });
			queryClient.invalidateQueries({ queryKey: accountKeys.lists() });
			message.success("Kunde gespeichert");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Speichern: ${error.message}`);
		},
	});
}

export function useDeleteAccount() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (id: string) => accountApi.delete(id),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: accountKeys.lists() });
			message.success("Kunde gelöscht");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Löschen: ${error.message}`);
		},
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
