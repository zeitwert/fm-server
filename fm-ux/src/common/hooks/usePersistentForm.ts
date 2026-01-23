/**
 * Hook for the edit/cancel/store pattern used in entity detail pages.
 *
 * Combines React Hook Form for form state with local state for edit mode.
 * Receives entity data and update mutation from outside, enabling clean separation of concerns.
 *
 * @example
 * const query = useAccountQuery(accountId);
 * const update = useUpdateAccount();
 *
 * const { form, isEditing, handleEdit, handleCancel, handleStore } = usePersistentForm({
 *   id: accountId,
 *   data: query.data,
 *   updateMutation: update,
 *   schema: accountFormSchema,
 * });
 */

import { useState, useEffect } from "react";
import { useForm, UseFormReturn, FieldValues, Resolver, FieldErrors } from "react-hook-form";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema";
import type { UseMutationResult } from "@tanstack/react-query";
import type { z } from "zod";
import { extractDirtyValues } from "../utils/formUtils";
import { transformToForm, transformFromForm } from "../utils/formTransformers";
import type { BaseEntity } from "../api/entityApi";
import type { EntityMeta } from "../api/jsonapi";

// ============================================================================
// Types
// ============================================================================

export interface UsePersistentFormOptions<T extends BaseEntity, TFormData extends FieldValues> {
	/** Entity ID */
	id: string;

	/** Entity data (from a query hook) */
	data: T | undefined;

	/** Pre-configured update mutation (from useUpdateEntity wrapper) */
	updateMutation: UseMutationResult<T, Error, Partial<T> & { id: string; meta?: EntityMeta }>;

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

	/** Callback when save succeeds (optional, e.g., to exit edit mode externally) */
	onSaveSuccess?: () => void;
}

export interface UsePersistentFormResult<TFormData extends FieldValues> {
	/** The React Hook Form instance */
	form: UseFormReturn<TFormData>;

	/** Whether the form is in edit mode */
	isEditing: boolean;

	/** Whether the form has unsaved changes */
	isDirty: boolean;

	/** Whether the form is currently being saved */
	isStoring: boolean;

	/** Enter edit mode */
	handleEdit: () => void;

	/** Cancel editing and reset form to server state */
	handleCancel: () => void;

	/** Save changes to the server (only dirty fields are sent) */
	handleStore: () => Promise<void>;
}

// ============================================================================
// Hook Implementation
// ============================================================================

export function usePersistentForm<T extends BaseEntity, TFormData extends FieldValues>(
	options: UsePersistentFormOptions<T, TFormData>
): UsePersistentFormResult<TFormData> {
	const {
		id,
		data,
		updateMutation,
		schema,
		transformToFormOverride,
		transformFromFormOverride,
		onSaveSuccess,
	} = options;

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

	// Sync form with entity data when it changes
	useEffect(() => {
		if (data) {
			const formData = transformToFormOverride
				? transformToFormOverride(data)
				: transformToForm<TFormData>(data, schema);
			// Explicitly reset dirty state to ensure form is clean after external updates
			// (e.g., workflow transitions that bypass the form's handleStore flow)
			form.reset(formData, {
				keepDirtyValues: false,
				keepDirty: false,
			});
		}
		// Note: 'form' is intentionally excluded from dependencies as it's a stable reference
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [data, transformToFormOverride, schema]);

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

			const result = await updateMutation.mutateAsync({
				id,
				...serverData,
				meta: { clientVersion: data?.meta?.version },
			});

			// Reset form after a short delay to ensure React's render cycle has settled
			// This mimics the timing of the old GET-after-save behavior
			setTimeout(() => {
				const newFormData = transformToFormOverride
					? transformToFormOverride(result)
					: transformToForm<TFormData>(result, schema);
				form.reset(newFormData);
			}, 50);

			setIsEditing(false);
			onSaveSuccess?.();
		},
		(errors: FieldErrors<TFormData>) => {
			console.error("[usePersistentForm] Validation failed; aborting save.", {
				entityId: id,
				errors,
			});
		}
	);

	// -------------------------------------------------------------------------
	// Return value
	// -------------------------------------------------------------------------

	return {
		form,
		isEditing,
		isDirty: form.formState.isDirty,
		isStoring: updateMutation.isPending,
		handleEdit,
		handleCancel,
		handleStore,
	};
}
