/**
 * Account API using the createEntityApi factory.
 *
 * Provides CRUD operations for Account entities with proper
 * JSONAPI serialization/deserialization.
 */

import { createEntityApi } from "../../common/api/entityApi";
import type { Account, AccountListItem } from "./types";

/**
 * Account API with all CRUD operations.
 *
 * API Endpoints:
 * - List: GET /api/account/accounts?include[account]=mainContact
 * - Get: GET /api/account/accounts/{id}?include[account]=...
 * - Create: POST /api/account/accounts
 * - Update: PATCH /api/account/accounts/{id}
 * - Delete: DELETE /api/account/accounts/{id}
 */
export const accountApi = createEntityApi<Account>({
	module: "account",
	path: "accounts",
	type: "account",
	includes: "include[account]=tenant,owner,accountType,clientSegment,mainContact,logo",
	relations: {
		tenant: "tenant",
		owner: "user",
		accountType: "accountType",
		clientSegment: "clientSegment",
		mainContact: "contact",
		logo: "document",
	},
});

/**
 * Account list API for fetching a lightweight list.
 * Uses fewer includes for better performance.
 */
export const accountListApi = createEntityApi<AccountListItem>({
	module: "account",
	path: "accounts",
	type: "account",
	includes: "include[account]=tenant,owner,accountType,clientSegment,mainContact",
	relations: {
		tenant: "tenant",
		owner: "user",
		accountType: "accountType",
		clientSegment: "clientSegment",
		mainContact: "contact",
	},
});
