import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface CaseStage {
	id: string;
	name: string;
	caseStageTypeId?: string;
	isAbstract?: boolean;
	abstractCaseStageId?: string;
	seqNr?: number;
}

export interface DocMeta extends EntityMeta {
	caseStage?: CaseStage;
	caseStages?: CaseStage[];
	assignee?: Enumerated;
	isInWork?: boolean;
}

export interface Task {
	id: string;
	meta?: DocMeta;
	subject: string;
	content?: string;
	isPrivate?: boolean;
	priority: Enumerated;
	dueAt?: string;
	remindAt?: string;
	relatedTo: Enumerated;
	owner: Enumerated;
	tenant: Enumerated;
	caseStage?: CaseStage;
	assignee?: Enumerated;
}

export interface TaskListItem {
	id: string;
	meta?: DocMeta;
	subject: string;
	priority: Enumerated;
	dueAt?: string;
	remindAt?: string;
	relatedTo: Enumerated;
}
