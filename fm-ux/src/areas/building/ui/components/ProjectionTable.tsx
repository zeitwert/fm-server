import { useMemo } from "react";
import { Table } from "antd";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import type { ProjectionPeriod } from "../../types";

// Number formatters matching fm-ui patterns
const CCY_FMT = new Intl.NumberFormat("de-CH", { maximumFractionDigits: 0 });
const PC_FMT = new Intl.NumberFormat("de-CH", {
	minimumFractionDigits: 0,
	maximumFractionDigits: 0,
});

// Row type for the table - can be a period row or a sub-row for restoration elements
interface ProjectionTableRow {
	id: string;
	year?: number;
	originalValue?: string;
	timeRate?: string;
	timeValue?: string;
	maintenanceCosts?: string;
	restorationCosts?: string;
	restorationPart?: string;
	isSubRow?: boolean;
}

interface ProjectionTableProps {
	periodList: ProjectionPeriod[];
}

export function ProjectionTable({ periodList }: ProjectionTableProps) {
	const { t } = useTranslation();

	// Transform period data into table rows (including sub-rows for multiple restoration elements)
	const tableData = useMemo(() => {
		return periodList.flatMap((period): ProjectionTableRow[] => {
			const rows: ProjectionTableRow[] = [];

			// Main row for the period
			const mainRow: ProjectionTableRow = {
				id: period.year.toString(),
				year: period.year,
				originalValue: CCY_FMT.format(period.originalValue),
				timeValue: CCY_FMT.format(period.timeValue),
				timeRate: PC_FMT.format((100.0 * period.timeValue) / period.originalValue),
				restorationCosts: period.restorationCosts ? CCY_FMT.format(period.restorationCosts) : "",
				maintenanceCosts: period.maintenanceCosts ? CCY_FMT.format(period.maintenanceCosts) : "",
				restorationPart: "",
			};

			// Handle restoration elements
			if (period.restorationElements.length === 1) {
				// Single element: show in main row
				mainRow.restorationPart = period.restorationElements[0].buildingPart.name;
			} else if (period.restorationElements.length > 1) {
				// Multiple elements: create sub-rows
				period.restorationElements.forEach((re, index) => {
					rows.push({
						id: `${period.year}-${index}`,
						restorationPart: re.buildingPart.name,
						restorationCosts: CCY_FMT.format(re.restorationCosts),
						isSubRow: true,
					});
				});
			}

			return [mainRow, ...rows];
		});
	}, [periodList]);

	const columns: ColumnType<ProjectionTableRow>[] = [
		{
			title: t("building:table.year"),
			dataIndex: "year",
			key: "year",
			width: "8%",
		},
		{
			title: t("building:table.originalValue"),
			dataIndex: "originalValue",
			key: "originalValue",
			width: "13%",
			align: "right",
		},
		{
			title: t("building:table.timeRate"),
			dataIndex: "timeRate",
			key: "timeRate",
			width: "8%",
			align: "right",
		},
		{
			title: t("building:table.timeValue"),
			dataIndex: "timeValue",
			key: "timeValue",
			width: "13%",
			align: "right",
		},
		{
			title: t("building:table.maintenanceCosts"),
			dataIndex: "maintenanceCosts",
			key: "maintenanceCosts",
			width: "12%",
			align: "right",
		},
		{
			title: t("building:table.restorationCosts"),
			dataIndex: "restorationCosts",
			key: "restorationCosts",
			width: "12%",
			align: "right",
		},
		{
			title: t("building:table.restorationPart"),
			dataIndex: "restorationPart",
			key: "restorationPart",
			width: "34%",
			ellipsis: true,
		},
	];

	return (
		<Table<ProjectionTableRow>
			className="af-projection-table"
			columns={columns}
			dataSource={tableData}
			rowKey="id"
			size="small"
			pagination={false}
			scroll={{ y: "calc(100vh - 380px)" }}
			rowClassName={(record) => (record.isSubRow ? "af-projection-subrow" : "")}
		/>
	);
}
