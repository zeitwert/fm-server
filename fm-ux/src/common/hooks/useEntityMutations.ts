/**
 * Generic mutation hooks for entity create and delete operations.
 *
 * These hooks centralize the common patterns for creating and deleting entities,
 * including cache invalidation and user feedback.
 */

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import type { BaseEntity } from "../api/entityApi";

// ============================================================================
// Types
// ============================================================================

export interface UseCreateEntityOptions<T extends BaseEntity> {
	/** Function to create the entity */
	createFn: (data: Omit<T, "id">) => Promise<T>;
	/** Query key for the list view (will be invalidated on success) */
	listQueryKey: readonly unknown[];
	/** Success message (optional, defaults to "Erstellt") */
	successMessage?: string;
	/** Error message prefix (optional, defaults to "Fehler beim Erstellen") */
	errorMessagePrefix?: string;
}

export interface UseDeleteEntityOptions {
	/** Function to delete the entity by ID */
	deleteFn: (id: string) => Promise<void>;
	/** Query key for the list view (will be invalidated on success) */
	listQueryKey: readonly unknown[];
	/** Success message (optional, defaults to "Gelöscht") */
	successMessage?: string;
	/** Error message prefix (optional, defaults to "Fehler beim Löschen") */
	errorMessagePrefix?: string;
}

// ============================================================================
// Hooks
// ============================================================================

/**
 * Generic hook for creating entities.
 *
 * Handles cache invalidation and user feedback automatically.
 *
 * @example
 * export function useCreateAccount() {
 *   return useCreateEntity<Account>({
 *     createFn: (data) => accountApi.create(data),
 *     listQueryKey: accountKeys.lists(),
 *     successMessage: "Kunde erstellt",
 *   });
 * }
 */
export function useCreateEntity<T extends BaseEntity>(options: UseCreateEntityOptions<T>) {
	const {
		createFn,
		listQueryKey,
		successMessage = "Erstellt",
		errorMessagePrefix = "Fehler beim Erstellen",
	} = options;

	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: createFn,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			message.success(successMessage);
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `${errorMessagePrefix}: ${error.message}`);
		},
	});
}

/**
 * Generic hook for deleting entities.
 *
 * Handles cache invalidation and user feedback automatically.
 *
 * @example
 * export function useDeleteAccount() {
 *   return useDeleteEntity({
 *     deleteFn: accountApi.delete,
 *     listQueryKey: accountKeys.lists(),
 *     successMessage: "Kunde gelöscht",
 *   });
 * }
 */
export function useDeleteEntity(options: UseDeleteEntityOptions) {
	const {
		deleteFn,
		listQueryKey,
		successMessage = "Gelöscht",
		errorMessagePrefix = "Fehler beim Löschen",
	} = options;

	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: deleteFn,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			message.success(successMessage);
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `${errorMessagePrefix}: ${error.message}`);
		},
	});
}
