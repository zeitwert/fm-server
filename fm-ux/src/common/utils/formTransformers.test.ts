import { describe, it, expect } from "vitest";
import { z } from "zod";
import { transformToForm, transformFromForm } from "./formTransformers";
import { displayOnly } from "./zodMeta";

describe("formTransformers", () => {
	// Test schema with various field types
	// Note: String fields use .optional() only (transformer handles null → "")
	// Enumerated/reference fields use .nullable() (form uses null for "no selection")
	const testSchema = z.object({
		name: z.string().min(1),
		description: z.string().optional(), // String: .optional() only
		count: z.number(),
		optionalNumber: z.number().optional(), // Number: .optional() only
		reference: z
			.object({
				id: z.string(),
				name: z.string(),
			})
			.nullable(), // Enumerated: .nullable() for "no selection"
		optionalReference: z
			.object({
				id: z.string(),
				name: z.string(),
			})
			.optional()
			.nullable(), // Enumerated: .nullable() for "no selection"
		items: z.array(z.string()).optional(),
	});

	type TestForm = z.infer<typeof testSchema>;

	interface TestEntity {
		name: string;
		description?: string;
		count: number;
		optionalNumber?: number;
		reference?: { id: string; name: string };
		optionalReference?: { id: string; name: string };
		items?: string[];
	}

	describe("transformToForm", () => {
		it("should convert undefined strings to empty string", () => {
			const entity: TestEntity = {
				name: "Test",
				count: 5,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.name).toBe("Test");
			expect(form.description).toBe(""); // undefined → ""
		});

		it("should preserve non-empty strings", () => {
			const entity: TestEntity = {
				name: "Test",
				description: "A description",
				count: 5,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.description).toBe("A description");
		});

		it("should convert undefined nullable objects to null", () => {
			const entity: TestEntity = {
				name: "Test",
				count: 5,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.reference).toBeNull(); // undefined → null
			expect(form.optionalReference).toBeNull(); // undefined → null
		});

		it("should preserve non-null object references", () => {
			const ref = { id: "1", name: "Ref" };
			const entity: TestEntity = {
				name: "Test",
				count: 5,
				reference: ref,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.reference).toEqual(ref);
		});

		it("should keep undefined numbers as undefined", () => {
			const entity: TestEntity = {
				name: "Test",
				count: 5,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.optionalNumber).toBeUndefined(); // undefined → undefined (non-nullable)
		});

		it("should preserve non-null numbers including zero", () => {
			const entity: TestEntity = {
				name: "Test",
				count: 5,
				optionalNumber: 0,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.optionalNumber).toBe(0);
		});

		it("should pass through arrays as-is", () => {
			const entity: TestEntity = {
				name: "Test",
				count: 5,
				items: ["a", "b"],
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.items).toEqual(["a", "b"]);
		});
	});

	describe("transformFromForm", () => {
		it("should convert empty strings to undefined", () => {
			const formData = {
				name: "Test",
				description: "",
			};

			const entity = transformFromForm<TestEntity, TestForm>(formData, testSchema);

			expect(entity.name).toBe("Test");
			expect(entity.description).toBeUndefined(); // "" → undefined
		});

		it("should preserve non-empty strings", () => {
			const formData = {
				description: "A description",
			};

			const entity = transformFromForm<TestEntity, TestForm>(formData, testSchema);

			expect(entity.description).toBe("A description");
		});

		it("should convert null to undefined for nullable reference fields", () => {
			const formData = {
				reference: null,
				optionalReference: null,
			};

			const entity = transformFromForm<TestEntity, TestForm>(formData, testSchema);

			expect(entity.reference).toBeUndefined(); // null → undefined
			expect(entity.optionalReference).toBeUndefined(); // null → undefined
		});

		it("should preserve non-null object references", () => {
			const ref = { id: "1", name: "Ref" };
			const formData = {
				reference: ref,
			};

			const entity = transformFromForm<TestEntity, TestForm>(formData, testSchema);

			expect(entity.reference).toEqual(ref);
		});

		it("should exclude display-only fields", () => {
			// Schema with display-only field marked via metadata
			const schemaWithDisplayOnly = z.object({
				name: z.string().min(1),
				items: displayOnly(z.array(z.string()).optional()),
			});

			const formData = {
				name: "Test",
				items: ["a", "b"],
			};

			const entity = transformFromForm<TestEntity>(formData, schemaWithDisplayOnly);

			expect(entity.name).toBe("Test");
			expect(entity.items).toBeUndefined(); // excluded as display-only
		});

		it("should pass through fields not in schema", () => {
			const formData = {
				name: "Test",
				unknownField: "value",
			};

			const entity = transformFromForm<TestEntity, TestForm>(
				formData as Record<string, unknown>,
				testSchema
			);

			expect((entity as Record<string, unknown>).unknownField).toBe("value");
		});

		it("should preserve numbers including zero", () => {
			const formData = {
				count: 0,
				optionalNumber: 0,
			};

			const entity = transformFromForm<TestEntity, TestForm>(formData, testSchema);

			expect(entity.count).toBe(0);
			expect(entity.optionalNumber).toBe(0);
		});
	});

	describe("nested transformations", () => {
		// Schema with nested objects and arrays
		const nestedSchema = z.object({
			name: z.string(),
			rating: z
				.object({
					id: z.string(),
					status: z.string().optional(),
					elements: z.array(
						z.object({
							id: z.string(),
							description: z.string().optional(),
							weight: z.number().optional(),
						})
					),
				})
				.optional(),
		});

		type NestedForm = z.infer<typeof nestedSchema>;

		interface NestedEntity {
			name: string;
			rating?: {
				id: string;
				status?: string;
				elements: Array<{
					id: string;
					description?: string;
					weight?: number;
				}>;
			};
		}

		describe("transformToForm - nested", () => {
			it("should convert null strings in nested objects to empty string", () => {
				const entity = {
					name: "Test",
					rating: {
						id: "r1",
						status: null,
						elements: [],
					},
				};
				const form = transformToForm<NestedForm>(entity, nestedSchema);
				expect(form.rating?.status).toBe(""); // null → ""
			});

			it("should convert null strings in nested arrays to empty string", () => {
				const entity = {
					name: "Test",
					rating: {
						id: "r1",
						elements: [
							{ id: "1", description: null, weight: 10 },
							{ id: "2", description: "has value", weight: null },
						],
					},
				};
				const form = transformToForm<NestedForm>(entity, nestedSchema);
				expect(form.rating?.elements[0].description).toBe(""); // null → ""
				expect(form.rating?.elements[0].weight).toBe(10);
				expect(form.rating?.elements[1].description).toBe("has value");
				expect(form.rating?.elements[1].weight).toBeUndefined(); // non-nullable number stays undefined
			});

			it("should handle deeply nested undefined strings", () => {
				const entity = {
					name: "Test",
					rating: {
						id: "r1",
						elements: [{ id: "1" }], // description and weight not present
					},
				};
				const form = transformToForm<NestedForm>(entity, nestedSchema);
				expect(form.rating?.elements[0].id).toBe("1");
				expect(form.rating?.elements[0].description).toBe(""); // undefined → ""
				expect(form.rating?.elements[0].weight).toBeUndefined(); // non-nullable stays undefined
			});

			it("should preserve non-null values in nested structures", () => {
				const entity: NestedEntity = {
					name: "Test",
					rating: {
						id: "r1",
						status: "active",
						elements: [{ id: "1", description: "desc", weight: 5 }],
					},
				};
				const form = transformToForm<NestedForm>(entity, nestedSchema);
				expect(form.rating?.status).toBe("active");
				expect(form.rating?.elements[0].description).toBe("desc");
				expect(form.rating?.elements[0].weight).toBe(5);
			});
		});

		describe("transformFromForm - nested", () => {
			it("should convert empty strings in nested objects to undefined", () => {
				const formData = {
					name: "Test",
					rating: {
						id: "r1",
						status: "",
						elements: [],
					},
				};
				const entity = transformFromForm<NestedEntity, NestedForm>(formData, nestedSchema);
				expect(entity.rating?.status).toBeUndefined(); // "" → undefined
			});

			it("should convert empty strings in nested arrays to undefined", () => {
				const formData = {
					name: "Test",
					rating: {
						id: "r1",
						elements: [
							{ id: "1", description: "", weight: 10 },
							{ id: "2", description: "has value", weight: 0 },
						],
					},
				};
				const entity = transformFromForm<NestedEntity, NestedForm>(formData, nestedSchema);
				expect(entity.rating?.elements[0].description).toBeUndefined(); // "" → undefined
				expect(entity.rating?.elements[0].weight).toBe(10);
				expect(entity.rating?.elements[1].description).toBe("has value");
				expect(entity.rating?.elements[1].weight).toBe(0); // 0 preserved
			});

			it("should preserve non-empty values in nested structures", () => {
				const formData = {
					name: "Test",
					rating: {
						id: "r1",
						status: "active",
						elements: [{ id: "1", description: "desc", weight: 5 }],
					},
				};
				const entity = transformFromForm<NestedEntity, NestedForm>(formData, nestedSchema);
				expect(entity.rating?.status).toBe("active");
				expect(entity.rating?.elements[0].description).toBe("desc");
				expect(entity.rating?.elements[0].weight).toBe(5);
			});
		});

		describe("nested round-trip", () => {
			it("should handle round-trip for nested structures", () => {
				const original: NestedEntity = {
					name: "Test",
					rating: {
						id: "r1",
						status: "active",
						elements: [
							{ id: "1", description: "desc1", weight: 10 },
							{ id: "2", description: "desc2", weight: 20 },
						],
					},
				};
				const form = transformToForm<NestedForm>(original, nestedSchema);
				const back = transformFromForm<NestedEntity, NestedForm>(
					form as Record<string, unknown>,
					nestedSchema
				);
				expect(back).toEqual(original);
			});

			it("should handle round-trip with null/empty values normalized", () => {
				// Server sends nulls for optional strings
				const serverEntity = {
					name: "Test",
					rating: {
						id: "r1",
						status: null,
						elements: [{ id: "1", description: null }],
					},
				};

				const form = transformToForm<NestedForm>(serverEntity, nestedSchema);
				expect(form.rating?.status).toBe(""); // null → ""
				expect(form.rating?.elements[0].description).toBe(""); // null → ""

				const back = transformFromForm<NestedEntity, NestedForm>(
					form as Record<string, unknown>,
					nestedSchema
				);
				expect(back.rating?.status).toBeUndefined(); // "" → undefined
				expect(back.rating?.elements[0].description).toBeUndefined(); // "" → undefined
			});
		});
	});

	describe("round-trip transformations", () => {
		it("should handle round-trip for complete entity", () => {
			const originalEntity: TestEntity = {
				name: "Complete Test",
				description: "Full description",
				count: 42,
				optionalNumber: 3.14,
				reference: { id: "ref-1", name: "Reference One" },
				optionalReference: { id: "ref-2", name: "Reference Two" },
				items: ["item1", "item2"],
			};

			const form = transformToForm<TestForm>(originalEntity, testSchema);
			const backToEntity = transformFromForm<TestEntity, TestForm>(
				form as Record<string, unknown>,
				testSchema
			);

			expect(backToEntity).toEqual(originalEntity);
		});

		it("should handle round-trip for sparse entity with display-only fields", () => {
			// Schema with items marked as display-only
			const schemaWithDisplayOnly = z.object({
				name: z.string().min(1),
				description: z.string().optional(),
				count: z.number(),
				optionalNumber: z.number().optional(),
				reference: z
					.object({
						id: z.string(),
						name: z.string(),
					})
					.nullable(),
				optionalReference: z
					.object({
						id: z.string(),
						name: z.string(),
					})
					.optional()
					.nullable(),
				items: displayOnly(z.array(z.string()).optional()),
			});

			const originalEntity: TestEntity = {
				name: "Sparse",
				count: 1,
				items: ["display-only"],
			};

			const form = transformToForm<TestForm>(originalEntity, schemaWithDisplayOnly);

			// items should be present in form
			expect(form.items).toEqual(["display-only"]);

			const backToEntity = transformFromForm<TestEntity>(
				form as Record<string, unknown>,
				schemaWithDisplayOnly
			);

			// items should be excluded when transforming back
			expect(backToEntity.items).toBeUndefined();
			expect(backToEntity.name).toBe("Sparse");
			expect(backToEntity.count).toBe(1);
		});
	});
});
