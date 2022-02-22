import { ReportEngineResult } from "../service/ReportEngine";
import { Datamart } from "./Datamart";
import { Layout } from "./Layout";

export interface Provider {
	/**
	 * The provider id
	 */
	id: string;

	/**
	 * The provider name
	 */
	name: string;

	/**
	 * Get list with parameters (possibly from a Template)
	 */
	list(
		datamart: Datamart,
		layout: Layout,
		params?: { [key: string]: any },
		sort?: string,
		limit?: number
	): Promise<ReportEngineResult>;

	/**
	 * Execute a report with parameters (possibly from a Template)
	 */
	execute(
		datamart: Datamart,
		layout: Layout,
		params?: { [key: string]: any },
		sort?: string,
		limit?: number
	): Promise<ReportEngineResult>;
}
