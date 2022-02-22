import { Datamart } from "./Datamart";
import { Layout } from "./Layout";

export interface Template {
	/**
	 * The datamart this template applies to
	 */
	datamart: Datamart;

	/**
	 * The template id
	 */
	id: string;

	/**
	 * The template name
	 */
	name: string;

	/**
	 * The layout this template is rendered with
	 */
	layout: Layout;

	/**
	 * The selection parameters
	 */
	params: Map<string, any>;

	/**
	 * The selection sort parameter
	 */
	sort?: string;

	/**
	 * The selection limit no. of items
	 */
	limit?: number;
}
