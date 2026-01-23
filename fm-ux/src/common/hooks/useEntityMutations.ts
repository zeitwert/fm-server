/**
 * Generic mutation hooks for entity CRUD operations.
 *
 * These hooks centralize the common patterns for creating, updating, and deleting entities,
 * including cache invalidation and user feedback.
 */

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { useTranslation } from "react-i18next";
import type { BaseEntity } from "../api/entityApi";
import type { EntityMeta } from "../api/jsonapi";

// ============================================================================
// Types
// ============================================================================

export interface UseCreateEntityOptions<T extends BaseEntity> {
	/** Function to create the entity */
	createFn: (data: Omit<T, "id">) => Promise<T>;
	/** Query key for the list view (will be invalidated on success) */
	listQueryKey: readonly unknown[];
	/** i18n translation key for success message (e.g., "account:message.created") */
	successMessageKey: string;
	/** i18n translation key for error message (optional, defaults to "common:message.createError") */
	errorMessageKey?: string;
}

export interface UseUpdateEntityOptions<T extends BaseEntity> {
	/** Function to update the entity */
	updateFn: (data: Partial<T> & { id: string; meta?: EntityMeta }) => Promise<T>;
	/** Query key prefix for the detail view (id will be appended for cache updates) */
	queryKey: readonly unknown[];
	/** Query key for the list view (will be invalidated on success) */
	listQueryKey?: readonly unknown[];
	/** i18n translation key for success message (e.g., "account:message.saved") */
	successMessageKey: string;
	/** i18n translation key for error message (optional, defaults to "common:message.saveError") */
	errorMessageKey?: string;
}

export interface UseDeleteEntityOptions {
	/** Function to delete the entity by ID */
	deleteFn: (id: string) => Promise<void>;
	/** Query key for the list view (will be invalidated on success) */
	listQueryKey: readonly unknown[];
	/** i18n translation key for success message (e.g., "account:message.deleted") */
	successMessageKey: string;
	/** i18n translation key for error message (optional, defaults to "common:message.deleteError") */
	errorMessageKey?: string;
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
 *     successMessageKey: "account:message.created",
 *   });
 * }
 */
export function useCreateEntity<T extends BaseEntity>(options: UseCreateEntityOptions<T>) {
	const {
		createFn,
		listQueryKey,
		successMessageKey,
		errorMessageKey = "common:message.createError",
	} = options;

	const { t } = useTranslation();
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: createFn,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			message.success(t(successMessageKey));
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || t(errorMessageKey, { message: error.message }));
		},
	});
}

/**
 * Generic hook for updating entities.
 *
 * Handles cache updates, list invalidation, and user feedback automatically.
 * The mutation function receives { id, ...changes, meta? } and returns the updated entity.
 *
 * @example
 * export function useUpdateAccount() {
 *   return useUpdateEntity<Account>({
 *     updateFn: accountApi.update,
 *     queryKey: accountKeys.details(),
 *     listQueryKey: accountKeys.lists(),
 *     successMessageKey: "account:message.saved",
 *   });
 * }
 */
export function useUpdateEntity<T extends BaseEntity>(options: UseUpdateEntityOptions<T>) {
	const {
		updateFn,
		queryKey,
		listQueryKey,
		successMessageKey,
		errorMessageKey = "common:message.saveError",
	} = options;

	const { t } = useTranslation();
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: updateFn,
		onSuccess: (updatedEntity) => {
			// Update the detail cache with the response data
			queryClient.setQueryData([...queryKey, updatedEntity.id], updatedEntity);
			// Invalidate list to ensure consistency
			if (listQueryKey) {
				queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			}
			message.success(t(successMessageKey));
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || t(errorMessageKey, { message: error.message }));
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
 *     successMessageKey: "account:message.deleted",
 *   });
 * }
 */
export function useDeleteEntity(options: UseDeleteEntityOptions) {
	const {
		deleteFn,
		listQueryKey,
		successMessageKey,
		errorMessageKey = "common:message.deleteError",
	} = options;

	const { t } = useTranslation();
	const queryClient = useQueryClient();

	return useMutation({
		mutationFn: deleteFn,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			message.success(t(successMessageKey));
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || t(errorMessageKey, { message: error.message }));
		},
	});
}
