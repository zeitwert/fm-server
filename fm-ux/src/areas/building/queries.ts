import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { useTranslation } from "react-i18next";
import { buildingApi, buildingListApi, fetchProjection } from "./api";
import type { Building } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";

export const buildingKeys = {
	all: ["building"] as const,
	lists: () => [...buildingKeys.all, "list"] as const,
	list: (params?: string) => [...buildingKeys.lists(), params] as const,
	details: () => [...buildingKeys.all, "detail"] as const,
	detail: (id: string) => [...buildingKeys.details(), id] as const,
	projections: () => [...buildingKeys.all, "projection"] as const,
	projection: (id: string) => [...buildingKeys.projections(), id] as const,
};

export function useBuildingList() {
	return useQuery({
		queryKey: buildingKeys.lists(),
		queryFn: () => buildingListApi.list(),
	});
}

export function useBuildingQuery(id: string) {
	return useQuery({
		queryKey: buildingKeys.detail(id),
		queryFn: () => buildingApi.get(id),
		enabled: !!id,
	});
}

export function useProjectionQuery(buildingId: string) {
	return useQuery({
		queryKey: buildingKeys.projection(buildingId),
		queryFn: () => fetchProjection(buildingId),
		enabled: !!buildingId,
	});
}

export function useCreateBuilding() {
	return useCreateEntity<Building>({
		createFn: (data) => buildingApi.create(data),
		listQueryKey: buildingKeys.lists(),
		successMessageKey: "building:message.created",
	});
}

export function useUpdateBuilding() {
	return useUpdateEntity<Building>({
		updateFn: buildingApi.update,
		queryKey: buildingKeys.details(),
		listQueryKey: buildingKeys.lists(),
		successMessageKey: "building:message.saved",
	});
}

export function useDeleteBuilding() {
	return useDeleteEntity({
		deleteFn: buildingApi.delete,
		listQueryKey: buildingKeys.lists(),
		successMessageKey: "building:message.deleted",
	});
}

export function useAddBuildingRating() {
	const queryClient = useQueryClient();
	const { t } = useTranslation();

	return useMutation({
		mutationFn: async ({ id, clientVersion }: { id: string; clientVersion?: number }) => {
			return buildingApi.update({
				id,
				meta: {
					clientVersion,
					operations: ["addRating", "calculationOnly"],
				},
			});
		},
		onSuccess: (_, variables) => {
			queryClient.invalidateQueries({ queryKey: buildingKeys.detail(variables.id) });
			message.success(t("building:message.ratingAdded"));
		},
		onError: (error) => {
			console.error("Add rating failed:", error);
			message.error(t("building:message.ratingAddFailed"));
		},
	});
}

export function useMoveRatingStatus() {
	const queryClient = useQueryClient();
	const { t } = useTranslation();

	return useMutation({
		mutationFn: async ({
			id,
			ratingStatusId,
			clientVersion,
		}: {
			id: string;
			ratingStatusId: string;
			clientVersion?: number;
		}) => {
			return buildingApi.update({
				id,
				currentRating: {
					id: "",
					ratingStatus: { id: ratingStatusId, name: "" },
				},
				meta: { clientVersion },
			});
		},
		onSuccess: (_, variables) => {
			queryClient.invalidateQueries({ queryKey: buildingKeys.detail(variables.id) });
			message.success(t("building:message.ratingStatusChanged"));
		},
		onError: (error) => {
			console.error("Move rating status failed:", error);
			message.error(t("building:message.ratingStatusChangeFailed"));
		},
	});
}

export function getBuildingQueryOptions(id: string) {
	return {
		queryKey: buildingKeys.detail(id),
		queryFn: () => buildingApi.get(id),
	};
}

export function getBuildingListQueryOptions() {
	return {
		queryKey: buildingKeys.lists(),
		queryFn: () => buildingListApi.list(),
	};
}
