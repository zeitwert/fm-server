/**
 * Entity API factory for creating typed CRUD APIs.
 *
 * Provides a generic factory that creates API functions for any entity type,
 * handling JSONAPI serialization/deserialization automatically.
 */

import { api, getApiUrl, API_CONTENT_TYPE } from "./client";
import {
	serialize,
	deserializeOne,
	deserializeMany,
	EntityConfig,
	EntityMeta,
	JsonApiDocument,
} from "./jsonapi";

// ============================================================================
// Types
// ============================================================================

/**
 * Configuration for creating an entity API.
 */
export interface EntityApiConfig extends EntityConfig {
	/** API module name (e.g., "building", "contact") */
	module: string;
	/** API path (e.g., "buildings", "contacts") */
	path: string;
	/** Include string for related resources (e.g., "include[building]=account,contacts") */
	includes?: string;
}

/**
 * Base entity type with optional id and meta.
 */
export interface BaseEntity {
	id?: string;
	meta?: EntityMeta;
}

/**
 * Entity API interface with CRUD operations.
 */
export interface EntityApi<T extends BaseEntity> {
	/**
	 * List all entities, optionally with query parameters.
	 * @param params - Query string (without leading ?)
	 */
	list(params?: string): Promise<T[]>;

	/**
	 * Get a single entity by ID.
	 */
	get(id: string): Promise<T>;

	/**
	 * Create a new entity.
	 * @param data - Entity data without id
	 */
	create(data: Omit<T, "id">): Promise<T>;

	/**
	 * Update an existing entity.
	 * @param data - Partial entity data with required id and optional clientVersion for optimistic locking
	 */
	update(data: Partial<T> & { id: string; meta?: EntityMeta }): Promise<T>;

	/**
	 * Delete an entity by ID.
	 */
	delete(id: string): Promise<void>;
}

// ============================================================================
// Factory
// ============================================================================

/**
 * Create a typed entity API with CRUD operations.
 *
 * Automatically handles JSONAPI serialization/deserialization and
 * constructs URLs with the proper module, path, and include parameters.
 *
 * @example
 * // Define the API
 * export const buildingApi = createEntityApi<Building>({
 *   module: "building",
 *   path: "buildings",
 *   type: "building",
 *   includes: "include[building]=account,contacts,coverFoto",
 *   relations: {
 *     account: "account",
 *     contacts: "contact",
 *     coverFoto: "document",
 *   },
 * });
 *
 * // Use with TanStack Query
 * const { data } = useQuery({
 *   queryKey: ["building", id],
 *   queryFn: () => buildingApi.get(id),
 * });
 *
 * // Create
 * const newBuilding = await buildingApi.create({ name: "New Building", ... });
 *
 * // Update with optimistic locking
 * const updated = await buildingApi.update({
 *   id: "123",
 *   name: "Updated Name",
 *   meta: { clientVersion: building.meta?.version },
 * });
 */
export function createEntityApi<T extends BaseEntity>(config: EntityApiConfig): EntityApi<T> {
	const { module, path, includes, ...serializerConfig } = config;

	/**
	 * Build the API URL with optional suffix and includes.
	 */
	const buildUrl = (suffix = "", withIncludes = true): string => {
		const base = getApiUrl(module, path + suffix);
		if (withIncludes && includes) {
			// Handle case where suffix already has query params
			const separator = suffix.includes("?") ? "&" : "?";
			return `${base}${separator}${includes}`;
		}
		return base;
	};

	/**
	 * Common headers for JSONAPI requests.
	 */
	const jsonApiHeaders = {
		"Content-Type": API_CONTENT_TYPE,
	};

	return {
		async list(params?: string): Promise<T[]> {
			// Build URL with optional params
			let url: string;
			if (params) {
				url = buildUrl(`?${params}`);
			} else if (includes) {
				url = buildUrl();
			} else {
				url = buildUrl("", false);
			}

			const response = await api.get<JsonApiDocument>(url);
			return deserializeMany<T>(response.data);
		},

		async get(id: string): Promise<T> {
			const url = buildUrl(`/${id}`);
			const response = await api.get<JsonApiDocument>(url);
			return deserializeOne<T>(response.data);
		},

		async create(data: Omit<T, "id">): Promise<T> {
			const url = buildUrl();
			const body = serialize(data as T, serializerConfig);

			// Remove id from the serialized data for create operations
			if (body.data && !Array.isArray(body.data)) {
				delete (body.data as { id?: string }).id;
			}

			const response = await api.post<JsonApiDocument>(url, body, {
				headers: jsonApiHeaders,
			});
			return deserializeOne<T>(response.data);
		},

		async update(data: Partial<T> & { id: string; meta?: EntityMeta }): Promise<T> {
			const url = buildUrl(`/${data.id}`);
			const body = serialize(data as T, serializerConfig);

			const response = await api.patch<JsonApiDocument>(url, body, {
				headers: jsonApiHeaders,
			});
			return deserializeOne<T>(response.data);
		},

		async delete(id: string): Promise<void> {
			const url = buildUrl(`/${id}`, false);
			await api.delete(url, {
				headers: jsonApiHeaders,
			});
		},
	};
}

// ============================================================================
// Query Options Helpers
// ============================================================================

/**
 * Create TanStack Query options for fetching a single entity.
 *
 * @example
 * const queryOptions = createQueryOptions(buildingApi, "building", id);
 * const { data } = useQuery(queryOptions);
 */
export function createQueryOptions<T extends BaseEntity>(
	entityApi: EntityApi<T>,
	queryKeyPrefix: string,
	id: string
) {
	return {
		queryKey: [queryKeyPrefix, id],
		queryFn: () => entityApi.get(id),
	};
}

/**
 * Create TanStack Query options for listing entities.
 *
 * @example
 * const queryOptions = createListQueryOptions(buildingApi, "buildings");
 * const { data } = useQuery(queryOptions);
 */
export function createListQueryOptions<T extends BaseEntity>(
	entityApi: EntityApi<T>,
	queryKeyPrefix: string,
	params?: string
) {
	return {
		queryKey: params ? [queryKeyPrefix, "list", params] : [queryKeyPrefix, "list"],
		queryFn: () => entityApi.list(params),
	};
}
