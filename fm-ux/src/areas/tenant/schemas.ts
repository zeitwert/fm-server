import { z } from "zod";
import type { Enumerated } from "../../common/types";
import { enumeratedSchema } from "../../common/utils/zodMeta";

export interface TenantCreationFormInput {
	name: string;
	description?: string;
	tenantType: Enumerated | null;
}

export const tenantCreationSchema = z.object({
	name: z.string().min(1, "tenant:message.validation.nameRequired"),
	description: z.string().optional(),
	tenantType: enumeratedSchema,
});

export type TenantCreationData = z.infer<typeof tenantCreationSchema>;

export interface TenantFormInput {
	name: string;
	description?: string | null;
	tenantType: Enumerated | null;
	inflationRate?: number | null;
	discountRate?: number | null;
}

export const tenantFormSchema = z.object({
	name: z.string().min(1, "tenant:message.validation.nameRequired"),
	description: z.string().optional().nullable(),
	tenantType: enumeratedSchema,
	inflationRate: z.number().optional().nullable(),
	discountRate: z.number().optional().nullable(),
});

export type TenantFormData = z.infer<typeof tenantFormSchema>;
