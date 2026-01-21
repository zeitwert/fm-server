import type { Enumerated } from "../../common/types";

export interface Contact {
	id: string;
	firstName?: string;
	lastName?: string;
	caption?: string;
	email?: string;
	phone?: string;
	mobile?: string;
	description?: string;
	birthDate?: string;
	contactRole?: Enumerated;
	salutation?: Enumerated;
	title?: Enumerated;
}
