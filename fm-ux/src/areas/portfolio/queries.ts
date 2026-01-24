import { useQuery } from "@tanstack/react-query";
import { portfolioApi, portfolioListApi } from "./api";
import type { Portfolio } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";

export const portfolioKeys = {
	all: ["portfolio"] as const,
	lists: () => [...portfolioKeys.all, "list"] as const,
	list: (params?: string) => [...portfolioKeys.lists(), params] as const,
	details: () => [...portfolioKeys.all, "detail"] as const,
	detail: (id: string) => [...portfolioKeys.details(), id] as const,
};

export function usePortfolioList() {
	return useQuery({
		queryKey: portfolioKeys.lists(),
		queryFn: () => portfolioListApi.list(),
	});
}

export function usePortfolioQuery(id: string) {
	return useQuery({
		queryKey: portfolioKeys.detail(id),
		queryFn: () => portfolioApi.get(id),
		enabled: !!id,
	});
}

export function useCreatePortfolio() {
	return useCreateEntity<Portfolio>({
		createFn: (data) => portfolioApi.create(data),
		listQueryKey: portfolioKeys.lists(),
		successMessageKey: "portfolio:message.created",
	});
}

export function useUpdatePortfolio() {
	return useUpdateEntity<Portfolio>({
		updateFn: portfolioApi.update,
		queryKey: portfolioKeys.details(),
		listQueryKey: portfolioKeys.lists(),
		successMessageKey: "portfolio:message.saved",
	});
}

export function useDeletePortfolio() {
	return useDeleteEntity({
		deleteFn: portfolioApi.delete,
		listQueryKey: portfolioKeys.lists(),
		successMessageKey: "portfolio:message.deleted",
	});
}

export function getPortfolioQueryOptions(id: string) {
	return {
		queryKey: portfolioKeys.detail(id),
		queryFn: () => portfolioApi.get(id),
	};
}

export function getPortfolioListQueryOptions() {
	return {
		queryKey: portfolioKeys.lists(),
		queryFn: () => portfolioListApi.list(),
	};
}
