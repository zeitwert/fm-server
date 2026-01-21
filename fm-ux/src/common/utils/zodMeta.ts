/**
 * Zod Schema Metadata Utilities
 *
 * Uses Zod's .describe() method to embed JSON metadata directly in field definitions.
 * This makes the schema the single source of truth for all field configuration.
 */

import { z } from "zod";

// ============================================================================
// Shared Schemas
// ============================================================================

/**
 * Zod schema for Enumerated type (code tables and entity references).
 * Use in form schemas for fields typed as `Enumerated | null`.
 */
export const enumeratedSchema = z
	.object({
		id: z.string(),
		name: z.string(),
	})
	.nullable();

// ============================================================================
// Types
// ============================================================================

export interface FieldMeta {
	/** Field is display-only and should not be included in form submission */
	isDisplayOnly?: boolean;
	// Extensible for future properties (e.g., isReadOnly, defaultValue, etc.)
}

// Zod v4 schema with description property
interface ZodV4SchemaWithDescription {
	description?: string;
}

// ============================================================================
// Metadata Helpers
// ============================================================================

/**
 * Create a JSON metadata string for use with .describe().
 *
 * @param meta Field metadata object
 * @returns JSON string to pass to .describe()
 */
export function fieldMeta(meta: FieldMeta): string {
	return JSON.stringify(meta);
}

/**
 * Extract metadata from a Zod schema field's description.
 * In Zod v4, description is stored directly on the schema object.
 *
 * @param schema The Zod schema field to extract metadata from
 * @returns Parsed FieldMeta object (empty object if no valid metadata)
 */
export function getFieldMeta(schema: unknown): FieldMeta {
	try {
		const zodSchema = schema as ZodV4SchemaWithDescription;
		const description = zodSchema.description;

		if (!description) {
			return {};
		}

		// Try to parse as JSON metadata
		const parsed = JSON.parse(description) as FieldMeta;
		return typeof parsed === "object" && parsed !== null ? parsed : {};
	} catch {
		// Not JSON or invalid structure - return empty metadata
		return {};
	}
}

/**
 * Check if a schema field is marked as display-only.
 *
 * @param schema The Zod schema field to check
 * @returns true if the field is display-only
 */
export function isDisplayOnlyField(schema: unknown): boolean {
	return getFieldMeta(schema).isDisplayOnly === true;
}

// ============================================================================
// Schema Wrappers
// ============================================================================

/**
 * Mark a Zod schema field as display-only.
 * Display-only fields are included in the form for viewing but excluded from submission.
 *
 * @param schema The Zod schema to wrap
 * @returns The same schema with display-only metadata
 *
 * @example
 * ```typescript
 * const formSchema = z.object({
 *   name: z.string(),
 *   contacts: displayOnly(z.array(z.any()).optional()),
 * });
 * ```
 */
export function displayOnly<T extends z.ZodTypeAny>(schema: T): T {
	return schema.describe(fieldMeta({ isDisplayOnly: true })) as T;
}
