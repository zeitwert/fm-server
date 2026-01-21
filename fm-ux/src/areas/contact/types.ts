import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface ContactAccount {
	id: string;
	caption: string;
}

export interface Contact {
	id: string;
	meta?: EntityMeta;
	firstName?: string;
	lastName?: string;
	name?: string;
	caption?: string;
	email?: string;
	phone?: string;
	mobile?: string;
	description?: string;
	birthDate?: string;
	contactRole?: Enumerated;
	salutation?: Enumerated;
	title?: Enumerated;
	account?: ContactAccount;
	tenant: Enumerated;
	owner: Enumerated;
}

export interface ContactListItem {
	id: string;
	caption?: string;
	firstName?: string;
	lastName?: string;
	email?: string;
	phone?: string;
	mobile?: string;
	account?: ContactAccount;
	owner: Enumerated;
}

export interface ContactFormData {
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
	tenant: Enumerated | null;
	owner: Enumerated | null;
}
