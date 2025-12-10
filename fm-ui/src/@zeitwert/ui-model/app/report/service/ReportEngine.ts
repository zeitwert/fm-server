import { Datamart } from "../model/Datamart";
import { Layout } from "../model/Layout";
import { Template } from "../model/Template";

export interface ReportEngineResult {
	data: any[];
	header: any[];
	layoutType: string;
	meta?: object;
}

/**
 * Report Engine (execution)
 */
export interface ReportEngine {
	/**
	 * Execute a template
	 */
	executeTemplate(template: Template): Promise<ReportEngineResult>;

	/**
	 * Execute a report with parameters
	 */
	execute(datamart: Datamart, layout: Layout, params?: { [key: string]: any }): Promise<ReportEngineResult>;
}
