/**
 * Account area exports.
 */

// Types
export type { Account, AccountListItem, AccountContact, AccountFormData } from "./types";

// Schemas
export { accountCreationSchema, accountFormSchema } from "./schemas";
export type { AccountCreationData } from "./schemas";

// API
export { accountApi, accountListApi } from "./api";

// Queries
export {
	accountKeys,
	useAccountList,
	useAccount,
	useCreateAccount,
	useUpdateAccount,
	useDeleteAccount,
	getAccountQueryOptions,
	getAccountListQueryOptions,
} from "./queries";

// UI Components
export { AccountArea } from "./ui/AccountArea";
export { AccountPage } from "./ui/AccountPage";
