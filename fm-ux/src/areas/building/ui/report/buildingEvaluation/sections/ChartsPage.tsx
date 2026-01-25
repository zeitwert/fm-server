import { useTranslation } from "react-i18next";
import type { Building, ProjectionResult } from "../../../../types";
import { ReportHeader, ReportFooter, PrintValueChart, PrintCostChart } from "../components";
import { useContainerSize } from "../../../../../../common/hooks";

interface ChartsPageProps {
	building: Building;
	projection: ProjectionResult;
	sectionNumber: number;
}

/**
 * Charts Page (Shapes 5 & 6 in Aspose)
 * Displays value chart (line) and cost chart (stacked bar).
 * Print-optimized layout matching the original Aspose template:
 * - Single section header
 * - No individual chart titles
 * - No borders
 * - Compact legends
 * - 30%/70% height split
 */
export function ChartsPage({ building, projection, sectionNumber }: ChartsPageProps) {
	const { t } = useTranslation();
	const [containerRef, containerSize] = useContainerSize<HTMLDivElement>();

	return (
		<div className="report-page report-page-content charts-page page-break">
			<ReportHeader building={building} />

			<div ref={containerRef} className="report-page-body charts-page-body">
				{/* Section header */}
				<h2 className="data-section-header">
					{sectionNumber}. {t("building:report.costChart")}, grafisch (alle Werte CHF)
				</h2>

				{/* Value Chart - Neuwert vs Zeitwert (30% height) */}
				<div className="chart-area chart-area-value">
					<PrintValueChart
						data={projection.periodList}
						containerWidth={containerSize.width}
					/>
				</div>

				{/* Cost Chart - Instandhaltung + Instandsetzung (70% height) */}
				<div className="chart-area chart-area-cost">
					<PrintCostChart data={projection.periodList} />
				</div>
			</div>

			<ReportFooter pageNumber={5} />
		</div>
	);
}
