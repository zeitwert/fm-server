import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { noteApi, noteListApi } from "./api";
import type { Note } from "./types";
import type { EntityMeta } from "../../common/api/jsonapi";

export const noteKeys = {
	all: ["note"] as const,
	lists: () => [...noteKeys.all, "list"] as const,
	list: (params?: string) => [...noteKeys.lists(), params] as const,
	details: () => [...noteKeys.all, "detail"] as const,
	detail: (id: string) => [...noteKeys.details(), id] as const,
};

export function useNoteList() {
	return useQuery({
		queryKey: noteKeys.lists(),
		queryFn: () => noteListApi.list(),
	});
}

export function useNote(id: string) {
	return useQuery({
		queryKey: noteKeys.detail(id),
		queryFn: () => noteApi.get(id),
		enabled: !!id,
	});
}

export function useUpdateNote() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (data: Partial<Note> & { id: string; meta?: EntityMeta }) => noteApi.update(data),
		onSuccess: (_, variables) => {
			queryClient.invalidateQueries({ queryKey: noteKeys.detail(variables.id) });
			queryClient.invalidateQueries({ queryKey: noteKeys.lists() });
			message.success("Notiz gespeichert");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Speichern: ${error.message}`);
		},
	});
}

export function useDeleteNote() {
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: (id: string) => noteApi.delete(id),
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: noteKeys.lists() });
			message.success("Notiz gelöscht");
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `Fehler beim Löschen: ${error.message}`);
		},
	});
}

export function getNoteQueryOptions(id: string) {
	return {
		queryKey: noteKeys.detail(id),
		queryFn: () => noteApi.get(id),
	};
}

export function getNoteListQueryOptions() {
	return {
		queryKey: noteKeys.lists(),
		queryFn: () => noteListApi.list(),
	};
}
