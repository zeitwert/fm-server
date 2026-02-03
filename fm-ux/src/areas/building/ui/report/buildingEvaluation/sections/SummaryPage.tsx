import { useTranslation } from "react-i18next";
import type { Building, BuildingElement, ProjectionResult } from "../../../../types";
import {
	getConditionColor,
	formatCHF,
	formatPercent,
	getShortTermCosts,
	getMidTermCosts,
	getLongTermCosts,
	getAverageMaintenanceCosts,
	calculateZNRatio,
} from "../../../../utils/evaluationUtils";
import { ReportHeader, ReportFooter } from "../components";
import { fontWeight } from "@/app/theme";

interface SummaryPageProps {
	building: Building;
	projection: ProjectionResult;
	inflationRate?: number;
	costChartImage?: string;
}

interface ElementSummaryRowProps {
	element: BuildingElement;
	maxWeight: number;
	startYear: number;
	endYear: number;
}

/**
 * Single element row in the summary grid
 */
function ElementSummaryRow({ element, maxWeight, startYear, endYear }: ElementSummaryRowProps) {
	const conditionColor = getConditionColor(element.condition);
	const weightBarWidth = maxWeight > 0 ? ((element.weight || 0) / maxWeight) * 100 : 0;
	const yearCount = endYear - startYear + 1;

	// Create year markers
	const yearMarkers = Array.from({ length: yearCount }, (_, i) => {
		const year = startYear + i;
		const isRestoration = element.restorationYear === year;
		return { year, isRestoration };
	});

	return (
		<div className="summary-element-row">
			<span className="summary-element-name">{element.buildingPart?.name || "-"}</span>
			<span>
				<div className="summary-weight-bar" style={{ width: `${weightBarWidth}%` }} />
			</span>
			<span>{formatPercent(element.weight)}</span>
			<span>
				<div className="summary-condition-dot" style={{ backgroundColor: conditionColor }} />
			</span>
			<span className="summary-year-markers">
				{yearMarkers.map(({ year, isRestoration }) => (
					<span key={year} className={`summary-year-marker ${isRestoration ? "active" : ""}`}>
						{isRestoration ? "●" : ""}
					</span>
				))}
			</span>
			<span>{element.condition ?? "-"}</span>
		</div>
	);
}

/**
 * Summary Page / One-Pager (Tables 5, 6, 7 and Shape 9 in Aspose)
 * Compact overview with:
 * - Basic data table (table 6)
 * - Evaluation parameters table (table 7)
 * - Element overview with weight bars and condition dots (table 5)
 * - Condensed cost chart (shape 9)
 */
export function SummaryPage({
	building,
	projection,
	inflationRate = 2,
	costChartImage,
}: SummaryPageProps) {
	const { t } = useTranslation();

	const elements = building.currentRating?.elements || [];
	const validElements = elements.filter((e) => e.weight && e.weight > 0);
	const maxWeight = Math.max(...validElements.map((e) => e.weight || 0), 1);

	const znRatio = calculateZNRatio(elements);
	const periods = projection.periodList;
	const firstPeriod = periods[0];
	const timeValue = firstPeriod ? Math.round(firstPeriod.timeValue) : 0;

	// Calculate total condition (weighted average)
	const totalCondition = znRatio;
	const totalConditionColor = getConditionColor(totalCondition);

	return (
		<div className="report-page report-page-content summary-page page-break">
			<ReportHeader building={building} />

			<div
				className="report-page-body"
				style={{ display: "flex", flexDirection: "column", gap: 16 }}
			>
				{/* Header with two tables side by side */}
				<div className="summary-header">
					{/* Basic Data Table (Table 6) */}
					<div className="summary-basic-data">
						<div className="summary-section">
							<h4 className="summary-section-title">{t("building:report.basicData")}</h4>
							<table className="summary-table">
								<tbody>
									<tr>
										<th>{t("building:label.buildingNr")}</th>
										<td>{building.buildingNr}</td>
									</tr>
									<tr>
										<th>{t("building:label.partCatalog")}</th>
										<td>{building.currentRating?.partCatalog?.name || "-"}</td>
									</tr>
									<tr>
										<th>{t("building:label.buildingYear")}</th>
										<td>{building.buildingYear || "-"}</td>
									</tr>
									<tr>
										<th>{t("building:report.insuredValue")}</th>
										<td>{formatCHF(building.insuredValue)}</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>

					{/* Evaluation Table (Table 7) */}
					<div className="summary-evaluation">
						<div className="summary-section">
							<h4 className="summary-section-title">{t("building:report.evaluationParams")}</h4>
							<table className="summary-table">
								<tbody>
									<tr>
										<th>{t("building:report.duration")}</th>
										<td>
											{projection.duration} {t("building:report.years")}
										</td>
									</tr>
									<tr>
										<th>{t("building:report.inflation")}</th>
										<td>{formatPercent(inflationRate)}</td>
									</tr>
									<tr>
										<th>
											{t("building:report.timeValue")} ({t("building:report.znRatio")})
										</th>
										<td>
											{formatCHF(timeValue)} ({formatPercent(znRatio)})
										</td>
									</tr>
									<tr>
										<th>{t("building:report.avgMaintenanceCosts")}</th>
										<td>{formatCHF(getAverageMaintenanceCosts(periods))}</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>

				{/* Element Overview Grid (Table 5) */}
				<div className="summary-elements">
					<div className="summary-section">
						<h4 className="summary-section-title">{t("building:report.elementOverview")}</h4>
						<div className="summary-elements-grid">
							{/* Header */}
							<div className="summary-elements-header">
								<span>{t("building:label.element")}</span>
								<span>{t("building:label.weight")}</span>
								<span>%</span>
								<span>{t("building:report.condition")}</span>
								<span>
									{projection.startYear} - {projection.endYear}
								</span>
								<span>Z/N</span>
							</div>

							{/* Total row first */}
							<div className="summary-element-row" style={{ fontWeight: fontWeight.bold }}>
								<span className="summary-element-name">{t("building:label.total")}</span>
								<span></span>
								<span>100%</span>
								<span>
									<div
										className="summary-condition-dot"
										style={{ backgroundColor: totalConditionColor }}
									/>
								</span>
								<span></span>
								<span>{totalCondition}</span>
							</div>

							{/* Element rows */}
							{validElements.map((element) => (
								<ElementSummaryRow
									key={element.id}
									element={element}
									maxWeight={maxWeight}
									startYear={projection.startYear}
									endYear={projection.endYear}
								/>
							))}
						</div>
					</div>
				</div>

				{/* Cost Chart (Shape 9) - Condensed */}
				<div className="summary-chart">
					{costChartImage ? (
						<img
							src={costChartImage}
							alt={t("building:report.costChart")}
							style={{ width: "100%", height: "100%", objectFit: "contain" }}
						/>
					) : (
						<div
							style={{
								width: "100%",
								height: "100%",
								display: "flex",
								alignItems: "center",
								justifyContent: "center",
								color: "#999",
								border: "1px dashed #ccc",
								borderRadius: 4,
								fontSize: "10pt",
							}}
						>
							{t("building:report.chartPlaceholder")}
						</div>
					)}
				</div>

				{/* Cost Summary */}
				<div className="summary-costs" style={{ marginTop: 8, fontSize: "9pt" }}>
					<div style={{ display: "flex", gap: 24, justifyContent: "flex-end" }}>
						<span>
							<strong>{t("building:report.shortTermCosts")}:</strong>{" "}
							{formatCHF(getShortTermCosts(periods))}
						</span>
						<span>
							<strong>{t("building:report.midTermCosts")}:</strong>{" "}
							{formatCHF(getMidTermCosts(periods))}
						</span>
						<span>
							<strong>{t("building:report.longTermCosts")}:</strong>{" "}
							{formatCHF(getLongTermCosts(periods))}
						</span>
					</div>
				</div>
			</div>

			<ReportFooter pageNumber={7} />
		</div>
	);
}
