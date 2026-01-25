import { z } from "zod";
import type { Enumerated } from "../../common/types";
import type { PortfolioObject } from "./types";
import { displayOnly, enumeratedSchema } from "../../common/utils/zodMeta";

export interface PortfolioCreationFormInput {
	name: string;
	portfolioNr?: string;
	description?: string;
	owner: Enumerated | null;
}

export const portfolioCreationSchema = z.object({
	name: z.string().min(1, "portfolio:message.validation.nameRequired"),
	portfolioNr: z.string().optional(),
	description: z.string().optional(),
	owner: enumeratedSchema,
});

export type PortfolioCreationData = z.infer<typeof portfolioCreationSchema>;

export interface PortfolioFormInput {
	name: string;
	portfolioNr?: string | null;
	description?: string | null;
	account?: Enumerated | null;
	owner: Enumerated | null;
	// Editable array fields
	includes?: PortfolioObject[];
	excludes?: PortfolioObject[];
	// Display-only field (computed by server)
	buildings?: PortfolioObject[];
}

export const portfolioFormSchema = z.object({
	name: z.string().min(1, "portfolio:message.validation.nameRequired"),
	portfolioNr: z.string().optional(),
	description: z.string().optional(),
	account: enumeratedSchema.optional().nullable(),
	owner: enumeratedSchema,
	// Editable array fields (submitted with form)
	includes: z.array(z.any()).optional(),
	excludes: z.array(z.any()).optional(),
	// Display-only field (computed by server, excluded from submission)
	buildings: displayOnly(z.array(z.any()).optional()),
});

export type PortfolioFormData = z.infer<typeof portfolioFormSchema>;
