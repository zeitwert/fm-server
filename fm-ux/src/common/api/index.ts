/**
 * API utilities for fm-ux.
 *
 * Re-exports all API-related functions and types for convenient importing.
 */

// HTTP client and URL helpers
export {
	api,
	getApiUrl,
	getRestUrl,
	getEnumUrl,
	getLogoUrl,
	JSON_CONTENT_TYPE,
	API_CONTENT_TYPE,
	SESSION_INFO_KEY,
	SESSION_STATE_KEY,
	TENANT_INFO_KEY,
} from "./client";

// JSONAPI serialization/deserialization
export {
	serialize,
	deserialize,
	deserializeOne,
	deserializeMany,
	cleanNulls,
	JSONAPI_HEADERS,
	type JsonApiDocument,
	type JsonApiResource,
	type JsonApiResourceIdentifier,
	type JsonApiRelationship,
	type EntityConfig,
	type EntityMeta,
} from "./jsonapi";

// Entity API factory
export {
	createEntityApi,
	createQueryOptions,
	createListQueryOptions,
	type EntityApiConfig,
	type EntityApi,
	type BaseEntity,
} from "./entityApi";
