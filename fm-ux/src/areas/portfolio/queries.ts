import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { portfolioApi, portfolioListApi } from "./api";
import type { Portfolio, PortfolioObject } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";
import type { EntityMeta } from "../../common/api/jsonapi";

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

interface CalculatePortfolioParams {
	id: string;
	includes: PortfolioObject[];
	excludes: PortfolioObject[];
	meta?: EntityMeta;
}

export function usePortfolioCalculate() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: async ({ id, includes, excludes, meta }: CalculatePortfolioParams) => {
			return portfolioApi.update({
				id,
				includes,
				excludes,
				meta: {
					...meta,
					operations: ["calculationOnly"],
				},
			});
		},
		onSuccess: (updatedPortfolio) => {
			// Update the detail cache with calculated result (not persisted on server)
			queryClient.setQueryData(portfolioKeys.detail(updatedPortfolio.id), updatedPortfolio);
		},
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
