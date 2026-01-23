/**
 * Hook for the edit/cancel/store pattern used in entity detail pages.
 *
 * Combines TanStack Query for server state, React Hook Form for form state,
 * and local state for edit mode into a single cohesive hook.
 */

import { useState, useEffect } from "react";
import { useForm, UseFormReturn, FieldValues, Resolver, FieldErrors } from "react-hook-form";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import type { z } from "zod";
import { extractDirtyValues } from "../utils/formUtils";
import { transformToForm, transformFromForm } from "../utils/formTransformers";
import type { BaseEntity } from "../api/entityApi";
import type { EntityMeta } from "../api/jsonapi";

// ============================================================================
// Types
// ============================================================================

export interface UseEntityQueriesOptions<T extends BaseEntity, TFormData extends FieldValues> {
	/** Entity ID to fetch */
	id: string;

	/** TanStack Query key prefix (id will be appended) */
	queryKey: string[];

	/** Function to fetch the entity by ID */
	queryFn: (id: string) => Promise<T>;

	/** Zod schema for form validation (must be a z.object() schema) */
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	schema: z.ZodType<TFormData, any, any>;

	/**
	 * Custom transform from entity to form data.
	 * If not provided, uses generic schema-based transformation.
	 */
	transformToFormOverride?: (entity: T) => TFormData;

	/**
	 * Custom transform from form data back to entity data.
	 * If not provided, uses generic schema-based transformation.
	 */
	transformFromFormOverride?: (formData: Partial<TFormData>) => Partial<T>;

	/** Function to update the entity */
	updateFn: (data: Partial<T> & { id: string; meta?: EntityMeta }) => Promise<T>;

	/** Success message (optional, defaults to "Gespeichert") */
	successMessage?: string;

	/** Error message prefix (optional, defaults to "Fehler beim Speichern") */
	errorMessagePrefix?: string;

	/** Query key for the list view (optional, will be invalidated on mutation success) */
	listQueryKey?: readonly unknown[];
}

export interface UseEntityQueriesResult<T extends BaseEntity, TFormData extends FieldValues> {
	// Data
	/** The fetched entity data */
	entity: T | undefined;
	/** Whether the entity is currently loading */
	isLoading: boolean;
	/** Whether an error occurred while fetching */
	isError: boolean;
	/** The error that occurred while fetching, if any */
	error: Error | null;

	// Form (React Hook Form instance)
	/** The React Hook Form instance */
	form: UseFormReturn<TFormData>;

	// Edit state
	/** Whether the form is in edit mode */
	isEditing: boolean;
	/** Whether the form has unsaved changes */
	isDirty: boolean;

	// Handlers
	/** Enter edit mode */
	handleEdit: () => void;
	/** Cancel editing and reset form to server state */
	handleCancel: () => void;
	/** Save changes to the server (only dirty fields are sent) */
	handleStore: () => Promise<void>;
	/** Update entity directly without going through the form (e.g., for stage transitions) */
	directMutation: (changes: Partial<T>) => Promise<void>;

	// Mutation state
	/** Whether the form is currently being saved */
	isStoring: boolean;
	/** The error that occurred while saving, if any */
	storeError: Error | null;
}

// ============================================================================
// Hook Implementation
// ============================================================================

export function useEntityQueries<T extends BaseEntity, TFormData extends FieldValues>(
	options: UseEntityQueriesOptions<T, TFormData>
): UseEntityQueriesResult<T, TFormData> {
	const {
		id,
		queryKey,
		queryFn,
		schema,
		transformToFormOverride,
		transformFromFormOverride,
		updateFn,
		successMessage = "Gespeichert",
		errorMessagePrefix = "Fehler beim Speichern",
		listQueryKey,
	} = options;

	const queryClient = useQueryClient();

	// -------------------------------------------------------------------------
	// Server state via TanStack Query
	// -------------------------------------------------------------------------

	const query = useQuery({
		queryKey: [...queryKey, id],
		queryFn: () => queryFn(id),
		enabled: !!id,
	});

	// -------------------------------------------------------------------------
	// Edit mode state (local)
	// -------------------------------------------------------------------------

	const [isEditing, setIsEditing] = useState(false);

	// -------------------------------------------------------------------------
	// Form state (local, ephemeral)
	// -------------------------------------------------------------------------

	const form = useForm<TFormData>({
		// Use standardSchemaResolver for Zod v4 compatibility
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		resolver: standardSchemaResolver(schema) as Resolver<TFormData, any>,
	});

	// Sync form with server data when it changes
	useEffect(() => {
		if (query.data) {
			const formData = transformToFormOverride
				? transformToFormOverride(query.data)
				: transformToForm<TFormData>(query.data, schema);
			form.reset(formData);
		}
	}, [query.data, transformToFormOverride, schema, form]);

	// -------------------------------------------------------------------------
	// Update mutation
	// -------------------------------------------------------------------------

	const mutation = useMutation({
		mutationFn: updateFn,
		onSuccess: (updatedEntity) => {
			// Use the response data directly instead of refetching
			queryClient.setQueryData([...queryKey, id], updatedEntity);
			// Invalidate list to ensure consistency
			if (listQueryKey) {
				queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			}
			setIsEditing(false);
			message.success(successMessage);
		},
		onError: (error: Error & { detail?: string }) => {
			message.error(error.detail || `${errorMessagePrefix}: ${error.message}`);
		},
	});

	// -------------------------------------------------------------------------
	// Handlers
	// -------------------------------------------------------------------------

	/**
	 * Enter edit mode.
	 */
	const handleEdit = () => {
		setIsEditing(true);
	};

	/**
	 * Cancel editing: reset form to server state and exit edit mode.
	 */
	const handleCancel = () => {
		form.reset();
		setIsEditing(false);
	};

	/**
	 * Save changes: submit only dirty fields with optimistic locking.
	 */
	const handleStore = form.handleSubmit(
		async (formData) => {
			const dirtyFields = form.formState.dirtyFields;
			const changedData = extractDirtyValues(
				formData as Record<string, unknown>,
				dirtyFields as Record<string, unknown>
			) as Partial<TFormData>;

			// Transform form data back to entity format
			const serverData = transformFromFormOverride
				? transformFromFormOverride(changedData)
				: transformFromForm<T>(changedData as Record<string, unknown>, schema);

			await mutation.mutateAsync({
				id,
				...serverData,
				meta: { clientVersion: query.data?.meta?.version },
			});
		},
		(errors: FieldErrors<TFormData>) => {
			console.error("[useEntityQueries] Validation failed; aborting save.", {
				entityId: id,
				errors,
			});
		}
	);

	/**
	 * Update entity directly without going through the form.
	 * Useful for actions like stage transitions that bypass form validation.
	 * Does not affect edit mode state.
	 */
	const directMutation = async (changes: Partial<T>): Promise<void> => {
		try {
			const updatedEntity = await updateFn({
				id,
				...changes,
				meta: { clientVersion: query.data?.meta?.version },
			});
			// Update cache with response
			queryClient.setQueryData([...queryKey, id], updatedEntity);
			// Invalidate list to ensure consistency
			if (listQueryKey) {
				queryClient.invalidateQueries({ queryKey: listQueryKey as unknown[] });
			}
		} catch (error) {
			const err = error as Error & { detail?: string };
			message.error(err.detail || `${errorMessagePrefix}: ${err.message}`);
			throw error;
		}
	};

	// -------------------------------------------------------------------------
	// Return value
	// -------------------------------------------------------------------------

	return {
		// Data
		entity: query.data,
		isLoading: query.isLoading,
		isError: query.isError,
		error: query.error,

		// Form
		form,

		// Edit state
		isEditing,
		isDirty: form.formState.isDirty,

		// Handlers
		handleEdit,
		handleCancel,
		handleStore,
		directMutation,

		// Mutation state
		isStoring: mutation.isPending,
		storeError: mutation.error,
	};
}
