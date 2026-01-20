/**
 * Account type definitions.
 *
 * Represents the Account entity for customer management.
 */

import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

/**
 * Contact reference within an account.
 * Read-only display of associated contacts.
 */
export interface AccountContact {
	id: string;
	name: string;
	email?: string;
	phone?: string;
	isMainContact?: boolean;
}

/**
 * Account entity from the API.
 *
 * Fields based on fm-ui AccountModel:
 * - name, description (text)
 * - accountType, clientSegment (Enumerated code tables)
 * - tenant, owner (Enumerated relations)
 * - inflationRate, discountRate (numbers)
 * - contacts (array relation, read-only)
 * - logo (document relation)
 */
export interface Account {
	id: string;
	meta?: EntityMeta;

	// Basic fields
	name: string;
	description?: string;

	// Code tables
	accountType: Enumerated;
	clientSegment?: Enumerated;

	// Relations
	tenant: Enumerated;
	owner: Enumerated;
	mainContact?: Enumerated;

	// Calculation parameters
	inflationRate?: number;
	discountRate?: number;

	// Logo (document relation)
	logo?: {
		id: string;
		name: string;
		contentTypeId?: string;
	};

	// Contacts (read-only array)
	contacts?: AccountContact[];
}

/**
 * Account list item (subset of fields for list display).
 */
export interface AccountListItem {
	id: string;
	name: string;
	accountType: Enumerated;
	clientSegment?: Enumerated;
	tenant: Enumerated;
	owner: Enumerated;
	mainContact?: Enumerated;
}

/**
 * Form data for creating/editing an account.
 * Separate from the entity to handle form-specific concerns.
 */
export interface AccountFormData {
	name: string;
	description?: string;
	accountType: Enumerated | null;
	clientSegment?: Enumerated | null;
	tenant: Enumerated | null;
	owner: Enumerated | null;
	inflationRate?: number | null;
	discountRate?: number | null;
}
