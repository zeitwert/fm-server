import { useQuery } from "@tanstack/react-query";
import { tenantApi, tenantListApi } from "./api";
import type { Tenant } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";

export const tenantKeys = {
	all: ["tenant"] as const,
	lists: () => [...tenantKeys.all, "list"] as const,
	list: (params?: string) => [...tenantKeys.lists(), params] as const,
	details: () => [...tenantKeys.all, "detail"] as const,
	detail: (id: string) => [...tenantKeys.details(), id] as const,
};

export function useTenantList() {
	return useQuery({
		queryKey: tenantKeys.lists(),
		queryFn: () => tenantListApi.list(),
	});
}

export function useTenantQuery(id: string) {
	return useQuery({
		queryKey: tenantKeys.detail(id),
		queryFn: () => tenantApi.get(id),
		enabled: !!id,
	});
}

export function useCreateTenant() {
	return useCreateEntity<Tenant>({
		createFn: (data) => tenantApi.create(data),
		listQueryKey: tenantKeys.lists(),
		successMessageKey: "tenant:message.created",
	});
}

export function useUpdateTenant() {
	return useUpdateEntity<Tenant>({
		updateFn: tenantApi.update,
		queryKey: tenantKeys.details(),
		listQueryKey: tenantKeys.lists(),
		successMessageKey: "tenant:message.saved",
	});
}

export function useDeleteTenant() {
	return useDeleteEntity({
		deleteFn: tenantApi.delete,
		listQueryKey: tenantKeys.lists(),
		successMessageKey: "tenant:message.deleted",
	});
}

export function getTenantQueryOptions(id: string) {
	return {
		queryKey: tenantKeys.detail(id),
		queryFn: () => tenantApi.get(id),
	};
}

export function getTenantListQueryOptions() {
	return {
		queryKey: tenantKeys.lists(),
		queryFn: () => tenantListApi.list(),
	};
}
