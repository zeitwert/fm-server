/**
 * TanStack Query hooks for Account data fetching.
 */

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { accountApi, accountListApi } from "./api";
import type { Account } from "./types";
import type { EntityMeta } from "../../common/api/jsonapi";

// Query keys
export const accountKeys = {
	all: ["account"] as const,
	lists: () => [...accountKeys.all, "list"] as const,
	list: (params?: string) => [...accountKeys.lists(), params] as const,
	details: () => [...accountKeys.all, "detail"] as const,
	detail: (id: string) => [...accountKeys.details(), id] as const,
};

/**
 * Hook for fetching the list of accounts.
 */
export function useAccountList() {
	return useQuery({
		queryKey: accountKeys.lists(),
		queryFn: () => accountListApi.list(),
	});
}

/**
 * Hook for fetching a single account by ID.
 */
export function useAccount(id: string) {
	return useQuery({
		queryKey: accountKeys.detail(id),
		queryFn: () => accountApi.get(id),
		enabled: !!id,
	});
}

/**
 * Hook for creating a new account.
 */
export function useCreateAccount() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (data: Omit<Account, "id">) => accountApi.create(data),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: accountKeys.lists() });
			message.success("Kunde erstellt");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Erstellen: ${error.message}`);
		},
	});
}

/**
 * Hook for updating an account.
 */
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

/**
 * Hook for deleting an account.
 */
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

/**
 * Helper to get query options for prefetching an account.
 */
export function getAccountQueryOptions(id: string) {
	return {
		queryKey: accountKeys.detail(id),
		queryFn: () => accountApi.get(id),
	};
}

/**
 * Helper to get query options for prefetching the account list.
 */
export function getAccountListQueryOptions() {
	return {
		queryKey: accountKeys.lists(),
		queryFn: () => accountListApi.list(),
	};
}
