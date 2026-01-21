import { createEntityApi } from "../../common/api/entityApi";
import type { Account, AccountListItem } from "./types";

export const accountApi = createEntityApi<Account>({
	module: "account",
	path: "accounts",
	type: "account",
	includes: "include[account]=mainContact,logo,contacts",
	relations: {
		mainContact: "contact",
		logo: "document",
		contacts: "contact",
	},
});

export const accountListApi = createEntityApi<AccountListItem>({
	module: "account",
	path: "accounts",
	type: "account",
	includes: "include[account]=mainContact",
	relations: {
		mainContact: "contact",
	},
});
