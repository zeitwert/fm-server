import { KanbanBoard } from "./KanbanBoard";
import { ReportLine } from "./ReportLine";

export enum LayoutType {
	Line = "line",
	Kanban = "kanban"
}

interface ReportViewerProps {
	className?: string;
	layout: any;
	data: any;
	templates?: any;
	maxColumns?: number;
	options?: any;
	onMouseEnter?: (itemId: string) => void;
	onMouseLeave?: (itemId: string) => void;
	onClick?: (itemId: string) => void;
}

export function ReportViewer(props: ReportViewerProps) {
	const { layout, data, templates, maxColumns, onMouseEnter, onMouseLeave, onClick } = props;
	if (!layout || !data?.data) {
		return null;
	}
	switch (layout.layoutType) {
		default:
		case LayoutType.Line:
			return <ReportLine
				items={data}
				templates={templates}
				maxColumns={maxColumns}
				{...props.options}
				onMouseEnter={onMouseEnter}
				onMouseLeave={onMouseLeave}
				onClick={onClick}
			/>;
		case LayoutType.Kanban:
			return <KanbanBoard items={data.data} {...layout.layout} {...props.options} />;
	}
}
