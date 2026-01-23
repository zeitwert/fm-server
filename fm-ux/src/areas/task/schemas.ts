import { z } from "zod";
import type { Enumerated } from "../../common/types";
import { enumeratedSchema, displayOnly } from "../../common/utils/zodMeta";

export interface TaskFormInput {
	subject: string;
	content?: string | null;
	isPrivate?: boolean;
	priority: Enumerated | null;
	dueAt?: string | null;
	remindAt?: string | null;
	relatedTo: Enumerated | null;
	owner: Enumerated | null;
}

export const taskFormSchema = z.object({
	subject: z.string().min(1, "task:message.validation.subjectRequired"),
	content: z.string().optional().nullable(),
	isPrivate: z.boolean().optional(),
	priority: enumeratedSchema,
	dueAt: z.string().optional().nullable(),
	remindAt: z.string().optional().nullable(),
	relatedTo: displayOnly(enumeratedSchema),
	owner: displayOnly(enumeratedSchema),
});

export type TaskFormData = z.infer<typeof taskFormSchema>;
