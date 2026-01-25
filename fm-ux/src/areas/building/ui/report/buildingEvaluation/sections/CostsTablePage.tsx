import { useMemo } from "react";
import { useTranslation } from "react-i18next";
import type { Building, ProjectionResult } from "../../../../types";
import { calculateAggregatedPeriods, formatNumber } from "../../../../utils/evaluationUtils";
import { ReportHeader, ReportFooter } from "../components";

interface CostsTablePageProps {
	building: Building;
	projection: ProjectionResult;
	sectionNumber: number;
}

// Row type for the table - can be a period row or a sub-row for restoration elements
interface CostsTableRow {
	id: string;
	year?: number;
	originalValue?: string;
	timeValue?: string;
	maintenanceCosts?: string;
	restorationCosts?: string;
	restorationPart?: string;
	totalCosts?: string;
	aggrCosts?: string;
	isSubRow?: boolean;
}

/**
 * Costs Table Page (Table 4 in Aspose)
 * Displays 25-year cost breakdown table with sub-rows for individual restoration elements.
 * May span multiple pages - rows have break-inside: avoid.
 *
 * Note: Header/footer shown at start/end of section. For multi-page content,
 * CSS @page running headers would be needed for per-page repetition.
 */
export function CostsTablePage({ building, projection, sectionNumber }: CostsTablePageProps) {
	const { t } = useTranslation();

	const periods = calculateAggregatedPeriods(projection.periodList);

	// Transform period data into table rows (including sub-rows for multiple restoration elements)
	// Following the same pattern as ProjectionTable.tsx
	const tableData = useMemo(() => {
		return periods.flatMap((period): CostsTableRow[] => {
			const rows: CostsTableRow[] = [];

			// Main row for the period
			const mainRow: CostsTableRow = {
				id: period.year.toString(),
				year: period.year,
				originalValue: formatNumber(period.originalValue),
				timeValue: formatNumber(period.timeValue),
				maintenanceCosts: formatNumber(period.maintenanceCosts),
				restorationCosts: period.restorationCosts > 0 ? formatNumber(period.restorationCosts) : "",
				restorationPart: "",
				totalCosts: formatNumber(period.totalCosts),
				aggrCosts: formatNumber(period.aggrCosts),
			};

			// Handle restoration elements
			if (period.restorationElements.length === 1) {
				// Single element: show in main row
				mainRow.restorationPart = period.restorationElements[0].buildingPart?.name || "";
			} else if (period.restorationElements.length > 1) {
				// Multiple elements: create sub-rows
				period.restorationElements.forEach((re, index) => {
					rows.push({
						id: `${period.year}-${index}`,
						restorationCosts: formatNumber(re.restorationCosts),
						restorationPart: re.buildingPart?.name || "",
						isSubRow: true,
					});
				});
			}

			return [mainRow, ...rows];
		});
	}, [periods]);

	return (
		<div className="report-page report-page-content costs-table-page page-break">
			<ReportHeader building={building} />

			<div className="report-page-body">
				<h2 className="data-section-header">
					{sectionNumber}. {t("building:report.costsTableTitle")}
				</h2>

				<table className="costs-table">
					<thead>
						<tr>
							<th>{t("building:table.year")}</th>
							<th>{t("building:table.originalValue")}</th>
							<th>{t("building:table.timeValue")}</th>
							<th>{t("building:table.maintenanceCosts")}</th>
							<th>{t("building:table.restorationCosts")}</th>
							<th>{t("building:table.restorationPart")}</th>
							<th>{t("building:report.totalCosts")}</th>
							<th>{t("building:report.cumulativeCosts")}</th>
						</tr>
					</thead>
					<tbody>
						{tableData.map((row) => (
							<tr
								key={row.id}
								className={row.isSubRow ? "costs-table-subrow" : ""}
							>
								<td>{row.year ?? ""}</td>
								<td>{row.originalValue ?? ""}</td>
								<td>{row.timeValue ?? ""}</td>
								<td>{row.maintenanceCosts ?? ""}</td>
								<td>{row.restorationCosts ?? ""}</td>
								<td className="element-name">{row.restorationPart ?? ""}</td>
								<td>{row.totalCosts ?? ""}</td>
								<td>{row.aggrCosts ?? ""}</td>
							</tr>
						))}
					</tbody>
				</table>
			</div>

			<ReportFooter pageNumber={6} />
		</div>
	);
}
