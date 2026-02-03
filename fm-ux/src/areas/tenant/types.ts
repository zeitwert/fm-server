import type { Enumerated } from "@/common/types";
import type { EntityMeta } from "@/common/api/jsonapi";

export interface Tenant {
	id: string;
	meta?: EntityMeta;
	name: string;
	description?: string;
	tenantType: Enumerated;
	owner?: Enumerated;
	inflationRate?: number;
	discountRate?: number;
	logo?: {
		id: string;
		name: string;
		contentTypeId?: string;
	};
}

export interface TenantListItem {
	id: string;
	name: string;
	tenantType: Enumerated;
	owner?: Enumerated;
}
