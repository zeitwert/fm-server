/**
 * Generic form transformers that convert between backend entity format and form format.
 *
 * The transformations are derived automatically from Zod schema introspection:
 * - String fields: undefined ↔ "" (empty string for controlled inputs)
 * - Nullable non-string fields: undefined ↔ null (null for "no selection" state)
 *
 * Display-only fields are detected via schema metadata (using displayOnly() helper).
 */

import type { z } from "zod";
import { isDisplayOnlyField } from "./zodMeta";

// ============================================================================
// Zod v4 Schema Introspection Helpers
// ============================================================================

// Zod v4 internal structure (not part of public API)
interface ZodV4Def {
	type: string;
	innerType?: unknown;
}

interface ZodV4Schema {
	_zod: {
		def: ZodV4Def;
	};
}

function getZodDef(schema: unknown): ZodV4Def {
	return (schema as ZodV4Schema)._zod.def;
}

/**
 * Check if a Zod schema is a string type (including optional/nullable wrappers).
 */
function isStringSchema(schema: unknown): boolean {
	const def = getZodDef(schema);

	if (def.type === "string") {
		return true;
	}

	// Unwrap optional/nullable/default wrappers
	if (def.type === "optional" || def.type === "nullable" || def.type === "default") {
		return isStringSchema(def.innerType);
	}

	return false;
}

/**
 * Check if a Zod schema is nullable (has .nullable() wrapper).
 */
function isNullableSchema(schema: unknown): boolean {
	const def = getZodDef(schema);

	if (def.type === "nullable") {
		return true;
	}

	// Check inside optional wrapper
	if (def.type === "optional" || def.type === "default") {
		return isNullableSchema(def.innerType);
	}

	return false;
}

// ============================================================================
// Generic Transformers
// ============================================================================

// Schema with shape property (for z.object() schemas)
interface ZodObjectLike {
	shape: Record<string, unknown>;
}

/**
 * Transform backend entity data to form data based on Zod schema.
 *
 * - String fields: undefined → ""
 * - Nullable non-string fields: undefined → null
 * - Other fields: pass through as-is
 *
 * @param entity The backend entity data
 * @param schema The Zod schema defining the form structure (must be z.object())
 * @returns Form-compatible data
 */
export function transformToForm<TForm>(
	entity: unknown,
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	schema: z.ZodType<TForm, any, any>
): TForm {
	const result: Record<string, unknown> = {};
	const entityRecord = entity as Record<string, unknown>;
	const objectSchema = schema as unknown as ZodObjectLike;

	for (const [key, fieldSchema] of Object.entries(objectSchema.shape)) {
		const value = entityRecord[key];

		if (isStringSchema(fieldSchema)) {
			// String fields: undefined → ""
			result[key] = value ?? "";
		} else if (isNullableSchema(fieldSchema)) {
			// Nullable non-string fields: undefined → null
			result[key] = value ?? null;
		} else {
			// Pass through as-is
			result[key] = value;
		}
	}

	return result as TForm;
}

/**
 * Transform form data back to backend entity format based on Zod schema.
 *
 * - String fields: "" → undefined
 * - Nullable fields: null → undefined
 * - Display-only fields (via schema metadata): excluded from result
 * - Other fields: pass through as-is
 *
 * @param formData The form data (typically only dirty/changed fields)
 * @param schema The Zod schema defining the form structure (must be z.object())
 * @returns Backend-compatible entity data (partial)
 */
export function transformFromForm<TEntity, TForm = unknown>(
	formData: Record<string, unknown>,
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	schema: z.ZodType<TForm, any, any>
): Partial<TEntity> {
	const result: Record<string, unknown> = {};
	const objectSchema = schema as unknown as ZodObjectLike;

	for (const [key, value] of Object.entries(formData)) {
		const fieldSchema = objectSchema.shape[key];

		// Skip display-only fields (detected from schema metadata)
		if (fieldSchema && isDisplayOnlyField(fieldSchema)) {
			continue;
		}

		if (!fieldSchema) {
			// Field not in schema, pass through
			result[key] = value;
		} else if (isStringSchema(fieldSchema)) {
			// String fields: "" → undefined
			result[key] = value || undefined;
		} else if (value === null) {
			// Nullable fields: null → undefined
			result[key] = undefined;
		} else {
			// Pass through as-is
			result[key] = value;
		}
	}

	return result as Partial<TEntity>;
}
