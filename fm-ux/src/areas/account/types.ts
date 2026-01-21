import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface AccountContact {
	id: string;
	caption: string;
	email?: string;
	phone?: string;
	isMainContact?: boolean;
}

export interface Account {
	id: string;
	meta?: EntityMeta;
	name: string;
	description?: string;
	accountType: Enumerated;
	clientSegment?: Enumerated;
	tenant: Enumerated;
	owner: Enumerated;
	mainContact?: Enumerated;
	inflationRate?: number;
	discountRate?: number;
	logo?: {
		id: string;
		name: string;
		contentTypeId?: string;
	};
	contacts?: AccountContact[];
}

export interface AccountListItem {
	id: string;
	name: string;
	accountType: Enumerated;
	clientSegment?: Enumerated;
	tenant: Enumerated;
	owner: Enumerated;
	mainContact?: Enumerated;
}

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
