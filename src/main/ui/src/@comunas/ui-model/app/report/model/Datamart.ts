import { Provider } from "./Provider";

/**
 * Parameter Types
 */
export enum DataType {
	string = "string",
	numeric = "number",
	date = "date",
	boolean = "boolean"
}

export interface Parameter {
	name: string;
	type: DataType;
	isList?: boolean;
	isIntl?: string;
}

export interface Datamart {
	/**
	 * The fully qualified datamart id (module.datamartId)
	 */
	id: string;

	/**
	 * The datamart name
	 */
	name: string;

	/**
	 * The module
	 */
	module: string;

	/**
	 * The datamart id
	 */
	datamart: string;

	/**
	 * The datamart provider
	 */
	provider: Provider;

	/**
	 * The supported selection parameters
	 */
	params: { [key: string]: Parameter };

	/**
	 * Provider specific configuration
	 */
	config: any;

	/**
	 * Validate supported values for assignment compatibility
	 */
	validateValues(params: { [key: string]: any }): void;
}
