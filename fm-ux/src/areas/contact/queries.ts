import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { contactApi, contactListApi } from "./api";
import type { Contact } from "./types";
import type { EntityMeta } from "../../common/api/jsonapi";

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

export function useContact(id: string) {
	return useQuery({
		queryKey: contactKeys.detail(id),
		queryFn: () => contactApi.get(id),
		enabled: !!id,
	});
}

export function useCreateContact() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (data: Omit<Contact, "id" | "tenant">) =>
			contactApi.create(data as Omit<Contact, "id">),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: contactKeys.lists() });
			message.success("Kontakt erstellt");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Erstellen: ${error.message}`);
		},
	});
}

export function useUpdateContact() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (data: Partial<Contact> & { id: string; meta?: EntityMeta }) =>
			contactApi.update(data),
		onSuccess: (_, variables) => {
			queryClient.invalidateQueries({ queryKey: contactKeys.detail(variables.id) });
			queryClient.invalidateQueries({ queryKey: contactKeys.lists() });
			message.success("Kontakt gespeichert");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Speichern: ${error.message}`);
		},
	});
}

export function useDeleteContact() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (id: string) => contactApi.delete(id),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: contactKeys.lists() });
			message.success("Kontakt gelöscht");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Löschen: ${error.message}`);
		},
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
