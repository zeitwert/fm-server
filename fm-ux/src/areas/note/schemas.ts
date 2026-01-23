import { z } from "zod";
import type { Enumerated } from "../../common/types";
import { displayOnly, enumeratedSchema } from "../../common/utils/zodMeta";

export interface NoteFormInput {
	subject?: string | null;
	content?: string | null;
	noteType: Enumerated | null;
	isPrivate?: boolean | null;
	relatedTo?: Enumerated | null;
}

export const noteFormSchema = z.object({
	subject: z.string().optional().nullable(),
	content: z.string().optional().nullable(),
	noteType: enumeratedSchema,
	isPrivate: z.boolean().optional().nullable(),
	relatedTo: displayOnly(enumeratedSchema.optional()),
});

export type NoteFormData = z.infer<typeof noteFormSchema>;
