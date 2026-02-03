import type { Enumerated } from "@/common/types";
import type { EntityMeta } from "@/common/api/jsonapi";

export interface User {
	id: string;
	meta?: EntityMeta;
	name: string;
	email: string;
	description?: string;
	password?: string;
	role: Enumerated;
	tenant?: Enumerated;
	owner?: Enumerated;
	avatar?: {
		id: string;
		name: string;
		contentTypeId?: string;
	};
}

export interface UserListItem {
	id: string;
	name: string;
	email: string;
	role: Enumerated;
	tenant?: Enumerated;
	owner?: Enumerated;
}
