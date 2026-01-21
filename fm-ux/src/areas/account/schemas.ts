import { z } from "zod";
import type { Enumerated } from "../../common/types";
import type { AccountContact } from "./types";

const enumeratedSchema = z
	.object({
		id: z.string(),
		name: z.string(),
	})
	.nullable();

export interface AccountCreationFormInput {
	name: string;
	description?: string;
	accountType: Enumerated | null;
	clientSegment?: Enumerated | null;
	tenant: Enumerated | null;
	owner: Enumerated | null;
}

export const accountCreationSchema = z.object({
	name: z.string().min(1, "Name ist erforderlich"),
	description: z.string().optional(),
	accountType: enumeratedSchema,
	clientSegment: enumeratedSchema.optional(),
	tenant: enumeratedSchema,
	owner: enumeratedSchema,
});

export type AccountCreationData = z.infer<typeof accountCreationSchema>;

export interface AccountFormInput {
	// Editable fields
	name: string;
	description?: string | null;
	accountType: Enumerated | null;
	clientSegment?: Enumerated | null;
	tenant: Enumerated | null;
	owner: Enumerated | null;
	inflationRate?: number | null;
	discountRate?: number | null;
	mainContact?: Enumerated | null;
	// Display-only fields (not validated, not submitted)
	contacts?: AccountContact[];
}

export const accountFormSchema = z.object({
	name: z.string().min(1, "Name ist erforderlich"),
	description: z.string().optional().nullable(),
	accountType: enumeratedSchema,
	clientSegment: enumeratedSchema.optional().nullable(),
	tenant: enumeratedSchema,
	owner: enumeratedSchema,
	inflationRate: z.number().optional().nullable(),
	discountRate: z.number().optional().nullable(),
	mainContact: enumeratedSchema.optional(),
	// contacts is display-only, no validation needed
	contacts: z.array(z.any()).optional(),
});

export type AccountFormData = z.infer<typeof accountFormSchema>;
