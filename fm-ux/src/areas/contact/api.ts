import { createEntityApi } from "../../common/api/entityApi";
import type { Contact, ContactListItem } from "./types";

export const contactApi = createEntityApi<Contact>({
	module: "contact",
	path: "contacts",
	type: "contact",
	includes: "include[contact]=account",
	relations: {
		account: "account",
	},
});

export const contactListApi = createEntityApi<ContactListItem>({
	module: "contact",
	path: "contacts",
	type: "contact",
	includes: "include[contact]=account",
	relations: {
		account: "account",
	},
});
