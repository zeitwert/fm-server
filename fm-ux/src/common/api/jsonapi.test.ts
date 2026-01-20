import { describe, it, expect } from "vitest";
import {
	serialize,
	deserialize,
	deserializeOne,
	deserializeMany,
	cleanNulls,
	JsonApiDocument,
	EntityConfig,
} from "./jsonapi";

// ============================================================================
// Test Types
// ============================================================================

interface TestBuilding {
	id?: string;
	name: string;
	description?: string;
	account?: { id: string; name?: string };
	contacts?: Array<{ id: string; name?: string }>;
	meta?: { version?: number; clientVersion?: number };
}

// ============================================================================
// Serialize Tests
// ============================================================================

describe("serialize", () => {
	const buildingConfig: EntityConfig = {
		type: "building",
		relations: {
			account: "account",
			contacts: "contact",
		},
	};

	it("serializes a simple object without relations", () => {
		const data: TestBuilding = {
			id: "123",
			name: "Test Building",
			description: "A test building",
		};

		const result = serialize(data, { type: "building" });

		expect(result).toEqual({
			data: {
				id: "123",
				type: "building",
				attributes: {
					name: "Test Building",
					description: "A test building",
				},
			},
		});
	});

	it("serializes single relation as reference", () => {
		const data: TestBuilding = {
			id: "123",
			name: "Test Building",
			account: { id: "456", name: "Test Account" },
		};

		const result = serialize(data, buildingConfig);

		expect(result.data).toMatchObject({
			relationships: {
				account: {
					data: { id: "456", type: "account" },
				},
			},
		});
		// Account should NOT be in attributes
		expect(
			(result.data as { attributes: Record<string, unknown> }).attributes.account
		).toBeUndefined();
	});

	it("serializes array relations as references", () => {
		const data: TestBuilding = {
			id: "123",
			name: "Test Building",
			contacts: [
				{ id: "c1", name: "Contact 1" },
				{ id: "c2", name: "Contact 2" },
			],
		};

		const result = serialize(data, buildingConfig);

		expect(result.data).toMatchObject({
			relationships: {
				contacts: {
					data: [
						{ id: "c1", type: "contact" },
						{ id: "c2", type: "contact" },
					],
				},
			},
		});
	});

	it("serializes null relation correctly", () => {
		const data: TestBuilding = {
			id: "123",
			name: "Test Building",
			account: undefined,
		};

		const result = serialize(data, buildingConfig);

		expect(result.data).toMatchObject({
			relationships: {
				account: { data: null },
			},
		});
	});

	it("omits id for create operations", () => {
		const data: Omit<TestBuilding, "id"> = {
			name: "New Building",
		};

		const result = serialize(data as TestBuilding, { type: "building" });

		expect((result.data as { id?: string }).id).toBeUndefined();
	});

	it("converts undefined attributes to null", () => {
		const data: TestBuilding = {
			id: "123",
			name: "Test Building",
			description: undefined,
		};

		const result = serialize(data, { type: "building" });

		expect(
			(result.data as { attributes: Record<string, unknown> }).attributes.description
		).toBeNull();
	});

	it("preserves meta for optimistic locking", () => {
		const data: TestBuilding = {
			id: "123",
			name: "Test Building",
			meta: { clientVersion: 5 },
		};

		const result = serialize(data, { type: "building" });

		expect((result.data as { meta?: { clientVersion?: number } }).meta).toEqual({
			clientVersion: 5,
		});
	});
});

// ============================================================================
// Deserialize Tests
// ============================================================================

describe("deserialize", () => {
	it("deserializes a single resource", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: {
					name: "Test Building",
					description: "A test description",
				},
			},
		};

		const result = deserialize<TestBuilding>(response);

		expect(result).toEqual({
			id: "123",
			name: "Test Building",
			description: "A test description",
		});
	});

	it("deserializes an array of resources", () => {
		const response: JsonApiDocument = {
			data: [
				{
					id: "1",
					type: "building",
					attributes: { name: "Building 1" },
				},
				{
					id: "2",
					type: "building",
					attributes: { name: "Building 2" },
				},
			],
		};

		const result = deserialize<TestBuilding>(response);

		expect(result).toHaveLength(2);
		expect(result).toEqual([
			{ id: "1", name: "Building 1" },
			{ id: "2", name: "Building 2" },
		]);
	});

	it("resolves single relationship from included", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: { name: "Test Building" },
				relationships: {
					account: {
						data: { id: "456", type: "account" },
					},
				},
			},
			included: [
				{
					id: "456",
					type: "account",
					attributes: { name: "Test Account", email: "test@example.com" },
				},
			],
		};

		const result = deserialize<TestBuilding>(response) as TestBuilding;

		expect(result.account).toEqual({
			id: "456",
			name: "Test Account",
			email: "test@example.com",
		});
	});

	it("resolves array relationships from included", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: { name: "Test Building" },
				relationships: {
					contacts: {
						data: [
							{ id: "c1", type: "contact" },
							{ id: "c2", type: "contact" },
						],
					},
				},
			},
			included: [
				{
					id: "c1",
					type: "contact",
					attributes: { name: "Contact 1" },
				},
				{
					id: "c2",
					type: "contact",
					attributes: { name: "Contact 2" },
				},
			],
		};

		const result = deserialize<TestBuilding>(response) as TestBuilding;

		expect(result.contacts).toHaveLength(2);
		expect(result.contacts).toEqual([
			{ id: "c1", name: "Contact 1" },
			{ id: "c2", name: "Contact 2" },
		]);
	});

	it("handles null relationship", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: { name: "Test Building" },
				relationships: {
					account: { data: null },
				},
			},
		};

		const result = deserialize<TestBuilding>(response) as TestBuilding;

		expect(result.account).toBeNull();
	});

	it("falls back to id-only when included is missing", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: { name: "Test Building" },
				relationships: {
					account: {
						data: { id: "456", type: "account" },
					},
				},
			},
			// No included array
		};

		const result = deserialize<TestBuilding>(response) as TestBuilding;

		expect(result.account).toEqual({ id: "456" });
	});

	it("preserves meta information", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: { name: "Test Building" },
				meta: { version: 5 },
			},
		};

		const result = deserialize<TestBuilding>(response) as TestBuilding;

		expect(result.meta).toEqual({ version: 5 });
	});

	it("returns null for null data", () => {
		const response: JsonApiDocument = {
			data: null,
		};

		const result = deserialize<TestBuilding>(response);

		expect(result).toBeNull();
	});
});

// ============================================================================
// deserializeOne Tests
// ============================================================================

describe("deserializeOne", () => {
	it("returns single resource", () => {
		const response: JsonApiDocument = {
			data: {
				id: "123",
				type: "building",
				attributes: { name: "Test Building" },
			},
		};

		const result = deserializeOne<TestBuilding>(response);

		expect(result).toEqual({ id: "123", name: "Test Building" });
	});

	it("throws for null data", () => {
		const response: JsonApiDocument = { data: null };

		expect(() => deserializeOne<TestBuilding>(response)).toThrow(
			"Expected single resource, got null"
		);
	});

	it("throws for array data", () => {
		const response: JsonApiDocument = {
			data: [{ id: "1", type: "building", attributes: { name: "Building 1" } }],
		};

		expect(() => deserializeOne<TestBuilding>(response)).toThrow(
			"Expected single resource, got array"
		);
	});
});

// ============================================================================
// deserializeMany Tests
// ============================================================================

describe("deserializeMany", () => {
	it("returns array of resources", () => {
		const response: JsonApiDocument = {
			data: [
				{ id: "1", type: "building", attributes: { name: "Building 1" } },
				{ id: "2", type: "building", attributes: { name: "Building 2" } },
			],
		};

		const result = deserializeMany<TestBuilding>(response);

		expect(result).toHaveLength(2);
	});

	it("returns empty array for null data", () => {
		const response: JsonApiDocument = { data: null };

		const result = deserializeMany<TestBuilding>(response);

		expect(result).toEqual([]);
	});

	it("throws for single object data", () => {
		const response: JsonApiDocument = {
			data: { id: "1", type: "building", attributes: { name: "Building 1" } },
		};

		expect(() => deserializeMany<TestBuilding>(response)).toThrow(
			"Expected array of resources, got single object"
		);
	});
});

// ============================================================================
// cleanNulls Tests
// ============================================================================

describe("cleanNulls", () => {
	it("removes null properties", () => {
		const obj = { a: 1, b: null, c: "test" };

		const result = cleanNulls(obj);

		expect(result).toEqual({ a: 1, c: "test" });
	});

	it("removes undefined properties", () => {
		const obj = { a: 1, b: undefined, c: "test" };

		const result = cleanNulls(obj);

		expect(result).toEqual({ a: 1, c: "test" });
	});

	it("cleans nested objects recursively", () => {
		const obj = {
			a: 1,
			nested: {
				b: 2,
				c: null,
				d: "test",
			},
		};

		const result = cleanNulls(obj);

		expect(result).toEqual({
			a: 1,
			nested: { b: 2, d: "test" },
		});
	});

	it("cleans arrays recursively", () => {
		const obj = {
			items: [
				{ id: 1, name: null },
				{ id: 2, name: "test" },
			],
		};

		const result = cleanNulls(obj);

		expect(result).toEqual({
			items: [{ id: 1 }, { id: 2, name: "test" }],
		});
	});

	it("preserves primitive values in arrays", () => {
		const obj = {
			values: [1, 2, null, 3],
		};

		const result = cleanNulls(obj);

		// null in array is preserved (only object properties are cleaned)
		expect(result).toEqual({ values: [1, 2, null, 3] });
	});
});

// ============================================================================
// Round-trip Tests
// ============================================================================

describe("serialize/deserialize round-trip", () => {
	const config: EntityConfig = {
		type: "building",
		relations: { account: "account" },
	};

	it("preserves data through serialize then deserialize", () => {
		const original: TestBuilding = {
			id: "123",
			name: "Test Building",
			description: "A description",
			account: { id: "456", name: "Account Name" },
		};

		// Serialize
		const serialized = serialize(original, config);

		// Create a mock server response by adding included
		const response: JsonApiDocument = {
			...serialized,
			included: [
				{
					id: "456",
					type: "account",
					attributes: { name: "Account Name" },
				},
			],
		};

		// Deserialize
		const result = deserializeOne<TestBuilding>(response);

		expect(result.id).toBe(original.id);
		expect(result.name).toBe(original.name);
		expect(result.description).toBe(original.description);
		expect(result.account).toEqual({ id: "456", name: "Account Name" });
	});
});
