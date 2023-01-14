import { AggregateMeta } from "../../aggregate/model/AggregateMeta";
import { Enumerated } from "../../aggregate/model/EnumeratedModel";
import { CaseStage } from "./BpmModel";

export interface DocMeta extends AggregateMeta {
	caseStage: CaseStage;
	isInWork: boolean;
	assignee: Enumerated;
	caseStages: CaseStage[];
	availableActions: string[];
}
