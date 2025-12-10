import { Datamart } from "../model/Datamart";
import { Layout } from "../model/Layout";
import { Provider } from "../model/Provider";
import { Template } from "../model/Template";

/**
 *
 * Reporting Engine
 *
 * Provider (f.ex. zeitwert Api)
 * - Datamart definitions
 *   name pattern: [module].[datamart] (f.ex. meeting.meetings, doc.docs)
 *   - Parameter definitions (typically selection criteria, like f.ex. doc_stage)
 *   - Layout definitions (bound to Datamart, specify rendering on screen)
 *     name pattern: [datamartName].[layout] (f.ex. meeting.meetings.default)
 *   - Template definitions (include Layout and Parameter values)
 *     name pattern: [datamartName].[template] (f.ex. meeting.meetings.my-open)
 */
export interface ReportService {
	/**
	 * Get provider by id
	 */
	provider(id: string): Provider;

	/**
	 * Get datamart list
	 */
	datamarts(): Promise<Datamart[]>;

	/**
	 * Get datamart by id ([module].[datamart])
	 */
	datamart(id: string): Promise<Datamart | undefined>;

	/**
	 * Get layouts for a given datamartId ([module].[datamart])
	 */
	layouts(datamartId: string): Promise<Layout[]>;

	/**
	 * Get layout by id ([module].[datamart].[layout])
	 */
	layout(id: string): Promise<Layout | undefined>;

	/**
	 * Get templates for a given datamartId ([module].[datamart])
	 */
	templates(datamartId: string): Promise<Template[]>;

	/**
	 * Get template by id ([module].[datamart].[template])
	 */
	template(id: string): Promise<Template | undefined>;
}
