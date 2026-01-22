import { z } from "zod";
import type { Enumerated } from "../../common/types";
import { enumeratedSchema } from "../../common/utils/zodMeta";

export interface ContactCreationFormInput {
	firstName?: string;
	lastName: string;
	email?: string;
	phone?: string;
	mobile?: string;
	salutation: Enumerated | null;
	contactRole?: Enumerated | null;
	account?: Enumerated | null;
	owner: Enumerated | null;
}

export const contactCreationSchema = z.object({
	firstName: z.string().optional(),
	lastName: z.string().min(1, "contact:message.validation.lastNameRequired"),
	email: z.string().optional(),
	phone: z.string().optional(),
	mobile: z.string().optional(),
	salutation: enumeratedSchema,
	contactRole: enumeratedSchema.optional().nullable(),
	account: enumeratedSchema.optional().nullable(),
	owner: enumeratedSchema,
});

export type ContactCreationData = z.infer<typeof contactCreationSchema>;

export interface ContactFormInput {
	firstName?: string | null;
	lastName: string;
	email?: string | null;
	phone?: string | null;
	mobile?: string | null;
	description?: string | null;
	birthDate?: string | null;
	contactRole?: Enumerated | null;
	salutation: Enumerated | null;
	title?: Enumerated | null;
	account?: Enumerated | null;
	owner: Enumerated | null;
}

export const contactFormSchema = z.object({
	firstName: z.string().optional().nullable(),
	lastName: z.string().min(1, "contact:message.validation.lastNameRequired"),
	email: z.string().optional().nullable(),
	phone: z.string().optional().nullable(),
	mobile: z.string().optional().nullable(),
	description: z.string().optional().nullable(),
	birthDate: z.string().optional().nullable(),
	contactRole: enumeratedSchema.optional().nullable(),
	salutation: enumeratedSchema,
	title: enumeratedSchema.optional().nullable(),
	account: enumeratedSchema.optional().nullable(),
	owner: enumeratedSchema,
});

export type ContactFormData = z.infer<typeof contactFormSchema>;
