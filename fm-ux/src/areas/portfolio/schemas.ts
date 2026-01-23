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
	// Display-only fields (not submitted via standard form save)
	includes?: PortfolioObject[];
	excludes?: PortfolioObject[];
	buildings?: PortfolioObject[];
}

export const portfolioFormSchema = z.object({
	name: z.string().min(1, "portfolio:message.validation.nameRequired"),
	portfolioNr: z.string().optional().nullable(),
	description: z.string().optional().nullable(),
	account: enumeratedSchema.optional().nullable(),
	owner: enumeratedSchema,
	// Display-only fields (excluded from submission via schema metadata)
	includes: displayOnly(z.array(z.any()).optional()),
	excludes: displayOnly(z.array(z.any()).optional()),
	buildings: displayOnly(z.array(z.any()).optional()),
});

export type PortfolioFormData = z.infer<typeof portfolioFormSchema>;
