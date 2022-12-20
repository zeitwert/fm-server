import { KanbanBoard } from "./KanbanBoard";
import { LineReport } from "./LineReport";

export enum LayoutType {
	Line = "line",
	Kanban = "kanban"
}

interface ReportViewerProps {
	className?: string;
	layout: any;
	data: any;
	dataTableCellTemplates?: any;
	maxColumns?: number;
	options?: any;
	sortProperty?: string;
	sortDirection?: "asc" | "desc" | undefined;
	onMouseEnter?: (itemId: string) => void;
	onMouseLeave?: (itemId: string) => void;
	onClick?: (itemId: string) => void;
	onSort?: (property: string, direction: "asc" | "desc" | undefined) => void;
	onSelectionChange?: (selectedItems: any[]) => void;
}

export function ReportViewer(props: ReportViewerProps) {
	const { layout, data, dataTableCellTemplates, maxColumns, sortProperty, sortDirection, onMouseEnter, onMouseLeave, onClick, onSort, onSelectionChange } = props;
	if (!layout || !data?.data) {
		return null;
	}
	switch (layout.layoutType) {
		default:
		case LayoutType.Line:
			return <LineReport
				items={data}
				dataTableCellTemplates={dataTableCellTemplates}
				maxColumns={maxColumns}
				sortProperty={sortProperty}
				sortDirection={sortDirection}
				{...props.options}
				onMouseEnter={onMouseEnter}
				onMouseLeave={onMouseLeave}
				onClick={onClick}
				onSort={onSort}
				onSelectionChange={onSelectionChange}
			/>;
		case LayoutType.Kanban:
			return <KanbanBoard
				items={data.data}
				{...layout.layout}
				{...props.options}
			/>;
	}
}
