import { useTranslation } from "react-i18next";
import type { Building, BuildingElement, ProjectionResult } from "../../../../types";
import { RENOVATION_MARKER, formatNumber } from "../../../../utils/evaluationUtils";
import { ReportHeader, ReportFooter } from "../components";

interface RenovationTimelinePageProps {
	building: Building;
	elements: BuildingElement[];
	projection: ProjectionResult;
	sectionNumber: number;
}

/**
 * Renovation Timeline Page (Table 3 in Aspose)
 * Displays a 25-year matrix with optimal renovation markers for each element.
 * Condensed font/spacing to fit 25+ columns on one landscape page.
 *
 * Layout:
 * - Grouped header row: Kurzfr. (2 years), Mittelfristig (4 years), Langfristig (20 years)
 * - Year columns with 90-degree rotated text
 * - Dashed vertical borders in timeline area
 */
export function RenovationTimelinePage({
	building,
	elements,
	projection,
	sectionNumber,
}: RenovationTimelinePageProps) {
	const { t } = useTranslation();

	// Filter to elements with weight > 0
	const validElements = elements.filter((e) => e.weight && e.weight > 0);

	// Generate year columns (26 years: 0-25 offset)
	const years = Array.from({ length: 26 }, (_, i) => projection.startYear + i);
	const minYear = projection.startYear;
	const maxYear = projection.startYear + 25;

	// Check if restoration year is within displayed range
	const isInRange = (year: number | undefined) =>
		year !== undefined && year >= minYear && year <= maxYear;

	// Time period spans: Kurzfr. (0-1), Mittelfristig (2-5), Langfristig (6-25)
	const shortTermCols = 2;
	const midTermCols = 4;
	const longTermCols = 20;

	// Calculate total restoration costs (only for elements with restoration in displayed range)
	const totalRestorationCosts = validElements
		.filter((e) => isInRange(e.restorationYear))
		.reduce((sum, e) => sum + (e.restorationCosts || 0), 0);

	return (
		<div className="report-page report-page-content renovation-timeline-page page-break">
			<ReportHeader building={building} />

			<div className="report-page-body">
				<h2 className="data-section-header">
					{sectionNumber}. {t("building:report.optimalRenovation")} (alle Kosten CHF)
				</h2>

				<table className="renovation-timeline">
					<thead>
						{/* Grouped time period header row */}
						<tr className="period-header-row">
							<th className="element-col">{t("building:label.element")}</th>
							<th className="period-col" colSpan={shortTermCols}>
								Kurzfr.
							</th>
							<th className="period-col" colSpan={midTermCols}>
								Mittelfristig
							</th>
							<th className="period-col" colSpan={longTermCols}>
								Langfristig
							</th>
							<th className="result-col">{t("building:report.year")}</th>
							<th className="result-col">{t("building:report.costs")}</th>
						</tr>
						{/* Year headers (rotated 90 degrees) */}
						<tr className="year-header-row">
							<th className="element-col"></th>
							{years.map((year) => (
								<th key={year} className="year-col">
									<span className="year-label">{year}</span>
								</th>
							))}
							<th className="result-col"></th>
							<th className="result-col"></th>
						</tr>
					</thead>
					<tbody>
						{validElements.map((element) => {
							const restorationYear = element.restorationYear;
							const inRange = isInRange(restorationYear);
							const delta = restorationYear
								? Math.max(0, restorationYear - projection.startYear)
								: null;

							return (
								<tr key={element.id}>
									<td className="element-name">{element.buildingPart?.name || "-"}</td>
									{years.map((year, index) => (
										<td key={year} className={`year-cell${delta === index ? " year-marker" : ""}`}>
											{delta === index ? <span className="marker-dot" /> : ""}
										</td>
									))}
									<td className="result-year">{inRange && restorationYear ? restorationYear : ""}</td>
									<td className="result-cost">
										{inRange && element.restorationCosts ? formatNumber(element.restorationCosts * 1000) : ""}
									</td>
								</tr>
							);
						})}

						{/* Total row */}
						<tr className="total-row">
							<td className="element-name">{t("building:label.total")}</td>
							{years.map((year) => (
								<td key={year} className="year-cell"></td>
							))}
							<td className="result-year"></td>
							<td className="result-cost">{formatNumber(totalRestorationCosts * 1000)}</td>
						</tr>
					</tbody>
				</table>
			</div>

			<ReportFooter pageNumber={4} />
		</div>
	);
}
