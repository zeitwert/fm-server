import { useQuery } from "@tanstack/react-query";
import { contactApi, contactListApi } from "./api";
import type { Contact } from "./types";
import { useCreateEntity, useUpdateEntity, useDeleteEntity } from "../../common/hooks";

export const contactKeys = {
	all: ["contact"] as const,
	lists: () => [...contactKeys.all, "list"] as const,
	list: (params?: string) => [...contactKeys.lists(), params] as const,
	details: () => [...contactKeys.all, "detail"] as const,
	detail: (id: string) => [...contactKeys.details(), id] as const,
};

export function useContactList() {
	return useQuery({
		queryKey: contactKeys.lists(),
		queryFn: () => contactListApi.list(),
	});
}

export function useContactQuery(id: string) {
	return useQuery({
		queryKey: contactKeys.detail(id),
		queryFn: () => contactApi.get(id),
		enabled: !!id,
	});
}

export function useCreateContact() {
	return useCreateEntity<Contact>({
		createFn: (data) => contactApi.create(data),
		listQueryKey: contactKeys.lists(),
		successMessageKey: "contact:message.created",
	});
}

export function useUpdateContact() {
	return useUpdateEntity<Contact>({
		updateFn: contactApi.update,
		queryKey: contactKeys.details(),
		listQueryKey: contactKeys.lists(),
		successMessageKey: "contact:message.saved",
	});
}

export function useDeleteContact() {
	return useDeleteEntity({
		deleteFn: contactApi.delete,
		listQueryKey: contactKeys.lists(),
		successMessageKey: "contact:message.deleted",
	});
}

export function getContactQueryOptions(id: string) {
	return {
		queryKey: contactKeys.detail(id),
		queryFn: () => contactApi.get(id),
	};
}

export function getContactListQueryOptions() {
	return {
		queryKey: contactKeys.lists(),
		queryFn: () => contactListApi.list(),
	};
}
