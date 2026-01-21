/**
 * JSONAPI serialization/deserialization utilities for fm-ux.
 *
 * Replaces the fm-ui JsonApi.ts implementation with a simpler,
 * TypeScript-first approach that returns plain objects instead
 * of the Repository format needed for MST stores.
 */

// ============================================================================
// Types
// ============================================================================

/**
 * JSONAPI resource identifier (used in relationships).
 */
export interface JsonApiResourceIdentifier {
	id: string;
	type: string;
}

/**
 * JSONAPI relationship data.
 */
export interface JsonApiRelationship {
	data: JsonApiResourceIdentifier | JsonApiResourceIdentifier[] | null;
}

/**
 * JSONAPI resource object.
 */
export interface JsonApiResource {
	id: string;
	type: string;
	attributes: Record<string, unknown>;
	relationships?: Record<string, JsonApiRelationship>;
	meta?: Record<string, unknown>;
}

/**
 * JSONAPI document (top-level response/request).
 */
export interface JsonApiDocument {
	data: JsonApiResource | JsonApiResource[] | null;
	included?: JsonApiResource[];
	meta?: { version?: number; [key: string]: unknown };
}

/**
 * Configuration for entity serialization.
 */
export interface EntityConfig {
	/** JSONAPI type name (e.g., "building", "contact") */
	type: string;
	/** Map of relation field names to their JSONAPI types */
	relations?: Record<string, string>;
}

/**
 * Meta information for optimistic locking.
 */
export interface EntityMeta {
	version?: number;
	clientVersion?: number;
	operations?: string[];
	[key: string]: unknown;
}

// ============================================================================
// Serialization (Client → Server)
// ============================================================================

/**
 * Serialize a plain object to JSONAPI format for POST/PATCH requests.
 *
 * - Handles relations by extracting { id } references
 * - Converts undefined to null (server requirement)
 * - Preserves meta for optimistic locking
 *
 * @example
 * const building = {
 *   id: "123",
 *   name: "Test Building",
 *   account: { id: "456", name: "Test Account" },
 * };
 * const jsonapi = serialize(building, {
 *   type: "building",
 *   relations: { account: "account" },
 * });
 * // Result: { data: { id: "123", type: "building", attributes: { name: "..." }, relationships: { account: { data: { id: "456", type: "account" } } } } }
 */
export function serialize<T extends { id?: string; meta?: EntityMeta }>(
	data: T,
	config: EntityConfig
): JsonApiDocument {
	const { id, meta, ...rest } = data;
	const attributes: Record<string, unknown> = {};
	const relationships: Record<string, JsonApiRelationship> = {};

	for (const [key, value] of Object.entries(rest)) {
		if (config.relations?.[key]) {
			// Handle relation - extract id reference
			const relationType = config.relations[key];

			if (Array.isArray(value)) {
				// Array of relations
				relationships[key] = {
					data: value
						.filter((v): v is { id: string } => v != null && typeof v === "object" && "id" in v)
						.map((v) => ({ id: v.id, type: relationType })),
				};
			} else if (value != null && typeof value === "object" && "id" in value) {
				// Single relation with id
				relationships[key] = {
					data: { id: (value as { id: string }).id, type: relationType },
				};
			} else {
				// Null or invalid relation
				relationships[key] = { data: null };
			}
		} else {
			// Regular attribute - convert undefined to null
			attributes[key] = value === undefined ? null : value;
		}
	}

	const resource: JsonApiResource = {
		id: id ?? "",
		type: config.type,
		attributes,
	};

	// Only add id if present (omit for create operations)
	if (!id) {
		delete (resource as Partial<JsonApiResource>).id;
	}

	// Only add relationships if there are any
	if (Object.keys(relationships).length > 0) {
		resource.relationships = relationships;
	}

	// Preserve meta for optimistic locking
	if (meta) {
		resource.meta = meta;
	}

	return { data: resource };
}

// ============================================================================
// Deserialization (Server → Client)
// ============================================================================

/**
 * Build a lookup map for included resources.
 */
function buildIncludedMap(included: JsonApiResource[] = []): Map<string, JsonApiResource> {
	const map = new Map<string, JsonApiResource>();
	for (const resource of included) {
		map.set(`${resource.type}:${resource.id}`, resource);
	}
	return map;
}

/**
 * Resolve a relationship reference to a full object using included data.
 */
function resolveRelation(
	ref: JsonApiResourceIdentifier,
	includedMap: Map<string, JsonApiResource>
): Record<string, unknown> {
	const included = includedMap.get(`${ref.type}:${ref.id}`);
	if (included) {
		const attributes = { ...included.attributes };
		if ("caption" in attributes) {
			attributes.name = attributes.caption;
		}
		// Return full included resource with flattened attributes
		return {
			id: included.id,
			...attributes,
		};
	}
	// Fallback: return just the reference id
	return { id: ref.id };
}

/**
 * Deserialize a single JSONAPI resource to a plain object.
 */
function deserializeResource<T>(
	resource: JsonApiResource,
	includedMap: Map<string, JsonApiResource>
): T {
	const result: Record<string, unknown> = {
		id: resource.id,
		...resource.attributes,
	};

	// Resolve relationships
	if (resource.relationships) {
		for (const [key, rel] of Object.entries(resource.relationships)) {
			if (rel.data === null) {
				result[key] = null;
			} else if (Array.isArray(rel.data)) {
				result[key] = rel.data.map((r) => resolveRelation(r, includedMap));
			} else {
				result[key] = resolveRelation(rel.data, includedMap);
			}
		}
	}

	// Preserve meta information
	if (resource.meta) {
		result.meta = resource.meta;
	}

	return result as T;
}

/**
 * Deserialize JSONAPI response to plain objects.
 *
 * - Flattens attributes to top level
 * - Converts relationships to full objects using included data
 * - Handles both single objects and arrays
 *
 * @example
 * const response = await api.get<JsonApiDocument>("/api/building/buildings/123");
 * const building = deserialize<Building>(response.data);
 * // building.account is now { id: "456", name: "Account Name", ... }
 */
export function deserialize<T>(response: JsonApiDocument): T | T[] | null {
	const { data, included = [] } = response;

	if (data === null) {
		return null;
	}

	const includedMap = buildIncludedMap(included);

	if (Array.isArray(data)) {
		return data.map((resource) => deserializeResource<T>(resource, includedMap));
	}

	return deserializeResource<T>(data, includedMap);
}

/**
 * Deserialize JSONAPI response expecting a single object.
 * Throws if the response contains an array or null.
 */
export function deserializeOne<T>(response: JsonApiDocument): T {
	const result = deserialize<T>(response);
	if (result === null) {
		throw new Error("Expected single resource, got null");
	}
	if (Array.isArray(result)) {
		throw new Error("Expected single resource, got array");
	}
	return result;
}

/**
 * Deserialize JSONAPI response expecting an array.
 * Returns empty array if response is null, throws if single object.
 */
export function deserializeMany<T>(response: JsonApiDocument): T[] {
	const result = deserialize<T>(response);
	if (result === null) {
		return [];
	}
	if (!Array.isArray(result)) {
		throw new Error("Expected array of resources, got single object");
	}
	return result;
}

// ============================================================================
// Utility Functions
// ============================================================================

/**
 * Remove null/undefined properties recursively.
 * Required for crnk server compatibility when sending requests.
 */
export function cleanNulls<T extends object>(obj: T): Partial<T> {
	const result: Partial<T> = {};

	for (const key of Object.keys(obj) as Array<keyof T>) {
		const value = obj[key];

		if (value == null) {
			// Skip null/undefined
			continue;
		}

		if (Array.isArray(value)) {
			// Recursively clean array items
			result[key] = value.map((item) =>
				typeof item === "object" && item !== null ? cleanNulls(item) : item
			) as T[keyof T];
		} else if (typeof value === "object") {
			// Recursively clean nested objects
			result[key] = cleanNulls(value as object) as T[keyof T];
		} else {
			result[key] = value;
		}
	}

	return result;
}

/**
 * Headers for JSONAPI requests.
 */
export const JSONAPI_HEADERS = {
	"Content-Type": "application/vnd.api+json",
} as const;
