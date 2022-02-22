import { AggregateMeta } from "../../aggregate/model/AggregateMeta";
import { CaseStage } from "./BpmModel";

export interface DocMeta extends AggregateMeta {
	caseStages: CaseStage[];
	availableActions: string[];
}
