/**
 * Form utilities for React Hook Form integration.
 */

/**
 * Extracts only the dirty (changed) values from form data based on React Hook Form's dirtyFields.
 *
 * This is useful for PATCH updates where we only want to send changed fields
 * to the server, reducing payload size and avoiding unintended overwrites.
 *
 * @example
 * const formData = { name: "Updated", street: "Same", city: "Same" };
 * const dirtyFields = { name: true };
 * const changed = extractDirtyValues(formData, dirtyFields);
 * // Result: { name: "Updated" }
 *
 * @example
 * // Nested objects
 * const formData = { address: { street: "New", city: "Same" } };
 * const dirtyFields = { address: { street: true } };
 * const changed = extractDirtyValues(formData, dirtyFields);
 * // Result: { address: { street: "New" } }
 */
export function extractDirtyValues<T extends Record<string, unknown>>(
	data: T,
	dirtyFields: Record<string, unknown>
): Partial<T> {
	const result: Partial<T> = {};

	for (const key of Object.keys(dirtyFields)) {
		const dirtyValue = dirtyFields[key];

		if (dirtyValue === true) {
			// Simple field is dirty - include its value
			result[key as keyof T] = data[key as keyof T];
		} else if (Array.isArray(dirtyValue)) {
			// Array field - include the entire array if any element is dirty
			result[key as keyof T] = data[key as keyof T];
		} else if (typeof dirtyValue === "object" && dirtyValue !== null) {
			// Nested object - recurse into it
			const nestedData = data[key as keyof T];
			if (nestedData && typeof nestedData === "object" && !Array.isArray(nestedData)) {
				const extracted = extractDirtyValues(
					nestedData as Record<string, unknown>,
					dirtyValue as Record<string, unknown>
				);
				// Only include if there are actual dirty values in the nested object
				if (Object.keys(extracted).length > 0) {
					result[key as keyof T] = extracted as T[keyof T];
				}
			}
		}
	}

	return result;
}

/**
 * Check if the form has any dirty fields.
 */
export function hasDirtyFields(dirtyFields: Record<string, unknown>): boolean {
	return Object.keys(dirtyFields).length > 0;
}
