import { useQuery } from "@tanstack/react-query";
import { noteApi, noteListApi } from "./api";
import type { Note } from "./types";
import { useDeleteEntity } from "../../common/hooks";

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

export function useDeleteNote() {
	return useDeleteEntity({
		deleteFn: noteApi.delete,
		listQueryKey: noteKeys.lists(),
		successMessage: "Notiz gelöscht",
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
