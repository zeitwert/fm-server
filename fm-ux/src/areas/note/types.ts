import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface Note {
	id: string;
	meta?: EntityMeta;
	relatedTo: Enumerated;
	noteType: Enumerated;
	subject?: string;
	content?: string;
	isPrivate?: boolean;
	tenant: Enumerated;
	owner: Enumerated;
}

export interface NoteListItem {
	id: string;
	relatedTo: Enumerated;
	noteType: Enumerated;
	subject?: string;
	owner: Enumerated;
	meta?: EntityMeta;
}
