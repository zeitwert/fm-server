export enum CaseStageType {
	initial = "initial",
	intermediate = "intermediate",
	terminal = "terminal",
	abstract = "abstract"
}

export interface CaseStage {
	id: string;
	name: string;
	description: string;
	due: number; // Days to expire
	caseDefId: string;
	caseStageTypeId: CaseStageType;
	isAbstract: boolean;
	seqNr: number;
	action: string;
	abstractCaseStageId?: string;
}
