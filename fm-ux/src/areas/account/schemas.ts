/**
 * Zod validation schemas for Account.
 *
 * Note: Due to Zod 4 compatibility issues with @hookform/resolvers,
 * we use simpler schemas and validate required fields at submit time.
 */

import { z } from "zod";
import type { Enumerated } from "../../common/types";

/**
 * Schema for Enumerated type (code table reference).
 * Nullable for form state (before selection).
 */
const enumeratedSchema = z
	.object({
		id: z.string(),
		name: z.string(),
	})
	.nullable();

/**
 * Form input type for account creation (allows nulls during editing).
 */
export interface AccountCreationFormInput {
	name: string;
	description?: string;
	accountType: Enumerated | null;
	clientSegment?: Enumerated | null;
	tenant: Enumerated | null;
	owner: Enumerated | null;
}

/**
 * Schema for creating an account - simple version.
 * Required field validation happens at submit time.
 */
export const accountCreationSchema = z.object({
	name: z.string().min(1, "Name ist erforderlich"),
	description: z.string().optional(),
	accountType: enumeratedSchema,
	clientSegment: enumeratedSchema.optional(),
	tenant: enumeratedSchema,
	owner: enumeratedSchema,
});

export type AccountCreationData = z.infer<typeof accountCreationSchema>;

/**
 * Form input type for editing accounts.
 */
export interface AccountFormInput {
	name: string;
	description?: string | null;
	accountType: Enumerated | null;
	clientSegment?: Enumerated | null;
	tenant: Enumerated | null;
	owner: Enumerated | null;
	inflationRate?: number | null;
	discountRate?: number | null;
}

/**
 * Schema for editing an account.
 */
export const accountFormSchema = z.object({
	name: z.string().min(1, "Name ist erforderlich"),
	description: z.string().optional().nullable(),
	accountType: enumeratedSchema,
	clientSegment: enumeratedSchema.optional().nullable(),
	tenant: enumeratedSchema,
	owner: enumeratedSchema,
	inflationRate: z.number().optional().nullable(),
	discountRate: z.number().optional().nullable(),
});

export type AccountFormData = z.infer<typeof accountFormSchema>;
