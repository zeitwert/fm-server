import { z } from "zod";
import type { Enumerated } from "@/common/types";
import { enumeratedSchema } from "@/common/utils/zodMeta";

export interface UserCreationFormInput {
	tenant: Enumerated | null;
	owner: Enumerated | null;
	email: string;
	name: string;
	password: string;
	role: Enumerated | null;
	description?: string;
}

export const userCreationSchema = z.object({
	tenant: enumeratedSchema,
	owner: enumeratedSchema,
	email: z
		.string()
		.email("user:message.validation.emailInvalid")
		.min(1, "user:message.validation.emailRequired"),
	name: z.string().min(1, "user:message.validation.nameRequired"),
	password: z.string().min(1, "user:message.validation.passwordRequired"),
	role: enumeratedSchema,
	description: z.string().optional(),
});

export type UserCreationData = z.infer<typeof userCreationSchema>;

export interface UserFormInput {
	name: string;
	email: string;
	description?: string | null;
	role: Enumerated | null;
	owner: Enumerated | null;
}

export const userFormSchema = z.object({
	name: z.string().min(1, "user:message.validation.nameRequired"),
	email: z
		.string()
		.email("user:message.validation.emailInvalid")
		.min(1, "user:message.validation.emailRequired"),
	description: z.string().optional(),
	role: enumeratedSchema,
	owner: enumeratedSchema,
});

export type UserFormData = z.infer<typeof userFormSchema>;
