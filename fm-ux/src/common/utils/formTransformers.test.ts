import { describe, it, expect } from "vitest";
import { z } from "zod";
import { transformToForm, transformFromForm } from "./formTransformers";
import { displayOnly } from "./zodMeta";

describe("formTransformers", () => {
	// Test schema with various field types
	const testSchema = z.object({
		name: z.string().min(1),
		description: z.string().optional().nullable(),
		count: z.number(),
		optionalNumber: z.number().optional().nullable(),
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

		it("should convert undefined nullable numbers to null", () => {
			const entity: TestEntity = {
				name: "Test",
				count: 5,
			};

			const form = transformToForm<TestForm>(entity, testSchema);

			expect(form.optionalNumber).toBeNull(); // undefined → null
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

		it("should convert null to undefined for nullable fields", () => {
			const formData = {
				reference: null,
				optionalNumber: null,
			};

			const entity = transformFromForm<TestEntity, TestForm>(formData, testSchema);

			expect(entity.reference).toBeUndefined(); // null → undefined
			expect(entity.optionalNumber).toBeUndefined(); // null → undefined
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
				description: z.string().optional().nullable(),
				count: z.number(),
				optionalNumber: z.number().optional().nullable(),
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
