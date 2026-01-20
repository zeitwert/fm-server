/**
 * Hook for the edit/cancel/store pattern used in entity detail pages.
 *
 * Combines TanStack Query for server state, React Hook Form for form state,
 * and local state for edit mode into a single cohesive hook.
 */

import { useState, useEffect } from "react";
import { useForm, UseFormReturn, FieldValues, Resolver } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import type { z } from "zod";
import { extractDirtyValues } from "../utils/formUtils";
import type { BaseEntity } from "../api/entityApi";
import type { EntityMeta } from "../api/jsonapi";

// ============================================================================
// Types
// ============================================================================

export interface UseEditableEntityOptions<T extends BaseEntity, TFormData extends FieldValues> {
	/** Entity ID to fetch */
	id: string;

	/** TanStack Query key prefix (id will be appended) */
	queryKey: string[];

	/** Function to fetch the entity by ID */
	queryFn: (id: string) => Promise<T>;

	/** Zod schema for form validation */
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	schema: z.ZodType<TFormData, any, any>;

	/** Transform entity data to form data (optional, defaults to identity) */
	transformToForm?: (entity: T) => TFormData;

	/** Transform form data back to entity data for updates (optional, defaults to identity) */
	transformFromForm?: (formData: Partial<TFormData>) => Partial<T>;

	/** Function to update the entity */
	updateFn: (data: Partial<T> & { id: string; meta?: EntityMeta }) => Promise<T>;

	/** Success message (optional, defaults to "Gespeichert") */
	successMessage?: string;

	/** Error message prefix (optional, defaults to "Fehler beim Speichern") */
	errorMessagePrefix?: string;
}

export interface UseEditableEntityResult<T extends BaseEntity, TFormData extends FieldValues> {
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

	// Mutation state
	/** Whether the form is currently being saved */
	isStoring: boolean;
	/** The error that occurred while saving, if any */
	storeError: Error | null;
}

// ============================================================================
// Hook Implementation
// ============================================================================

export function useEditableEntity<T extends BaseEntity, TFormData extends FieldValues>(
	options: UseEditableEntityOptions<T, TFormData>
): UseEditableEntityResult<T, TFormData> {
	const {
		id,
		queryKey,
		queryFn,
		schema,
		transformToForm,
		transformFromForm,
		updateFn,
		successMessage = "Gespeichert",
		errorMessagePrefix = "Fehler beim Speichern",
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
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		resolver: zodResolver(schema) as Resolver<TFormData, any>,
	});

	// Sync form with server data when it changes
	useEffect(() => {
		if (query.data) {
			const formData = transformToForm
				? transformToForm(query.data)
				: (query.data as unknown as TFormData);
			form.reset(formData);
		}
	}, [query.data, transformToForm, form]);

	// -------------------------------------------------------------------------
	// Update mutation
	// -------------------------------------------------------------------------

	const mutation = useMutation({
		mutationFn: updateFn,
		onSuccess: () => {
			// Invalidate the query to refetch fresh data
			queryClient.invalidateQueries({ queryKey: [...queryKey, id] });
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
	const handleStore = form.handleSubmit(async (formData) => {
		const dirtyFields = form.formState.dirtyFields;
		const changedData = extractDirtyValues(
			formData as Record<string, unknown>,
			dirtyFields as Record<string, unknown>
		) as Partial<TFormData>;

		// Transform form data back to entity format if needed
		const serverData = transformFromForm
			? transformFromForm(changedData)
			: (changedData as unknown as Partial<T>);

		await mutation.mutateAsync({
			id,
			...serverData,
			meta: { clientVersion: query.data?.meta?.version },
		});
	});

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

		// Mutation state
		isStoring: mutation.isPending,
		storeError: mutation.error,
	};
}
