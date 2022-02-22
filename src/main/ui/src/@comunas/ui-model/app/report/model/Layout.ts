import { Datamart } from "./Datamart";

export enum LayoutType {
	Line = "line",
	Kanban = "kanban"
}

export interface Layout {
	/**
	 * The datamart this layout applies to
	 */
	datamart: Datamart;

	/**
	 * The fully qualified id (datamart.layout)
	 */
	id: string;

	/**
	 * The layout name
	 */
	name: string;

	/**
	 * The layout type
	 */
	layoutType: LayoutType;

	/**
	 * The layout definition
	 */
	layout: any;
}
