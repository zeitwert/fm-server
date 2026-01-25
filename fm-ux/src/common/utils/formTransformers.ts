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

/**
 * Check if a Zod schema is an object type (z.object()).
 */
function isObjectSchema(schema: unknown): boolean {
	const def = getZodDef(schema);
	if (def.type === "object") return true;
	if (def.type === "optional" || def.type === "nullable" || def.type === "default") {
		return isObjectSchema(def.innerType);
	}
	return false;
}

/**
 * Check if a Zod schema is an array type (z.array()).
 */
function isArraySchema(schema: unknown): boolean {
	const def = getZodDef(schema);
	if (def.type === "array") return true;
	if (def.type === "optional" || def.type === "nullable" || def.type === "default") {
		return isArraySchema(def.innerType);
	}
	return false;
}

/**
 * Get the inner schema from optional/nullable/default wrappers.
 */
function unwrapSchema(schema: unknown): unknown {
	const def = getZodDef(schema);
	if (def.type === "optional" || def.type === "nullable" || def.type === "default") {
		return unwrapSchema(def.innerType);
	}
	return schema;
}

// Zod v4 array def structure
interface ZodV4ArrayDef extends ZodV4Def {
	element?: unknown;
}

/**
 * Get the element schema from an array schema.
 */
function getArrayElementSchema(schema: unknown): unknown {
	const unwrapped = unwrapSchema(schema);
	const def = getZodDef(unwrapped) as ZodV4ArrayDef;
	return def.element;
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
 * Recursively handles nested objects and arrays:
 * - String fields: null/undefined → ""
 * - Nullable non-string fields: null/undefined → null
 * - Arrays: recursively transform each element
 * - Objects: recursively transform each field
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
	return transformValueToForm(entity, schema) as TForm;
}

/**
 * Recursively transform a value to form format based on its schema.
 */
function transformValueToForm(value: unknown, schema: unknown): unknown {
	// Handle null/undefined based on schema type
	if (value === null || value === undefined) {
		if (isStringSchema(schema)) {
			return ""; // null/undefined string → ""
		} else if (isNullableSchema(schema)) {
			return null; // nullable non-string → null (for dropdowns/references)
		}
		return undefined; // non-nullable field: null/undefined → undefined
	}

	// Recurse into arrays
	if (isArraySchema(schema) && Array.isArray(value)) {
		const elementSchema = getArrayElementSchema(schema);
		return value.map((item) => transformValueToForm(item, elementSchema));
	}

	// Recurse into objects (but not nullable object schemas which represent references)
	if (isObjectSchema(schema) && typeof value === "object" && !isNullableSchema(schema)) {
		const objectSchema = unwrapSchema(schema) as ZodObjectLike;
		const result: Record<string, unknown> = {};
		for (const [key, fieldSchema] of Object.entries(objectSchema.shape)) {
			const fieldValue = (value as Record<string, unknown>)[key];
			result[key] = transformValueToForm(fieldValue, fieldSchema);
		}
		return result;
	}

	// Primitive or nullable object reference: pass through
	return value;
}

/**
 * Transform form data back to backend entity format based on Zod schema.
 *
 * Recursively handles nested objects and arrays:
 * - String fields: "" → undefined
 * - Nullable fields: null → undefined
 * - Display-only fields (via schema metadata): excluded from result
 * - Arrays: recursively transform each element
 * - Objects: recursively transform each field
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
	return transformValueFromForm(formData, schema, true) as Partial<TEntity>;
}

/**
 * Recursively transform a value from form format to backend format.
 *
 * @param value The form value to transform
 * @param schema The Zod schema for this value
 * @param isTopLevel Whether this is the top-level object (for display-only field handling)
 */
function transformValueFromForm(value: unknown, schema: unknown, isTopLevel = false): unknown {
	// Handle null → undefined
	if (value === null) {
		return undefined;
	}

	// Handle undefined
	if (value === undefined) {
		return undefined;
	}

	// String fields: "" → undefined
	if (isStringSchema(schema)) {
		return value === "" ? undefined : value;
	}

	// Recurse into arrays
	if (isArraySchema(schema) && Array.isArray(value)) {
		const elementSchema = getArrayElementSchema(schema);
		return value.map((item) => transformValueFromForm(item, elementSchema));
	}

	// Recurse into objects (but not nullable object schemas which represent references)
	if (isObjectSchema(schema) && typeof value === "object" && !isNullableSchema(schema)) {
		const objectSchema = unwrapSchema(schema) as ZodObjectLike;
		const result: Record<string, unknown> = {};

		for (const [key, fieldValue] of Object.entries(value as Record<string, unknown>)) {
			const fieldSchema = objectSchema.shape[key];

			// Skip display-only fields at top level (detected from schema metadata)
			if (isTopLevel && fieldSchema && isDisplayOnlyField(fieldSchema)) {
				continue;
			}

			if (!fieldSchema) {
				// Field not in schema, pass through
				result[key] = fieldValue;
			} else {
				result[key] = transformValueFromForm(fieldValue, fieldSchema);
			}
		}
		return result;
	}

	// Primitive or nullable object reference: pass through
	return value;
}
