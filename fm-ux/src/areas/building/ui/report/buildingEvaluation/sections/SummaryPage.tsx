import { useTranslation } from "react-i18next";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, ResponsiveContainer, ReferenceLine } from "recharts";
import type { Building, BuildingElement, ProjectionResult } from "../../../../types";
import { COLORS } from "../../../components/projectionChartConfig";
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

interface SummaryPageProps {
	building: Building;
	projection: ProjectionResult;
	inflationRate?: number;
}

// =============================================================================
// CONSTANTS
// =============================================================================

/** Number of years to display in the timeline */
const YEAR_COUNT = 26;

/** Z/N axis labels (100 to 50 in steps of 10) */
const ZN_AXIS_LABELS = [100, 90, 80, 70, 60, 50];

// =============================================================================
// HELPER COMPONENTS
// =============================================================================

interface CostChartData {
	year: number;
	maintenance: number;
	restoration: number;
}

interface SummaryCostChartProps {
	data: CostChartData[];
}

/**
 * Recharts-based stacked bar chart for cost visualization.
 */
function SummaryCostChart({ data }: SummaryCostChartProps) {
	const { t } = useTranslation();

	// Generate year grid lines for CSS overlay
	const yearCount = data.length;

	return (
		<div className="summary-cost-chart">
			<div className="cost-chart-wrapper">
				{/* Y-axis area */}
				<div className="cost-chart-yaxis">
					<ResponsiveContainer width="100%" height="100%">
						<BarChart data={data} margin={{ top: 5, right: 45, left: 0, bottom: 25 }}>
							<YAxis
								tick={{ fontSize: 9, fill: "#333" }}
								width={45}
								orientation="left"
								yAxisId="left"
								axisLine={false}
								tickLine={false}
							/>
							{/* Hidden bars needed for Y-axis scale calculation */}
							<Bar dataKey="maintenance" fill="transparent" yAxisId="left" />
							<Bar dataKey="restoration" fill="transparent" yAxisId="left" />
						</BarChart>
					</ResponsiveContainer>
				</div>
				{/* Chart area with CSS grid overlay for year columns */}
				<div
					className="cost-chart-bars"
					style={{ "--chart-year-count": yearCount } as React.CSSProperties}
				>
					{/* CSS grid overlay for year column borders */}
					<div className="cost-chart-grid-overlay">
						{data.map((_, i) => (
							<div key={i} className="cost-chart-grid-cell" />
						))}
					</div>
					{/* Recharts bar chart */}
					<div className="cost-chart-content">
						<ResponsiveContainer width="100%" height="100%">
							<BarChart
								data={data}
								margin={{ top: 5, right: 0, left: 0, bottom: 10 }}
								barCategoryGap="8%"
								barGap={0}
							>
								<CartesianGrid strokeDasharray="3 3" vertical={false} />
								<XAxis
									dataKey="year"
									tick={{ fontSize: 9, fill: "#333", dx: -4 }}
									angle={-90}
									textAnchor="end"
									height={20}
									interval={0}
									tickLine={false}
									axisLine={false}
								/>
								<YAxis hide />
								<ReferenceLine y={0} stroke="#333" strokeWidth={1} />
								<Bar dataKey="maintenance" fill={COLORS.maintenanceCosts} />
								<Bar dataKey="restoration" fill={COLORS.restorationCosts} />
							</BarChart>
						</ResponsiveContainer>
					</div>
				</div>
			</div>
			{/* Legend below chart */}
			<div className="cost-chart-legend">
				<span className="legend-item">
					<span className="legend-color legend-maintenance" />
					{t("building:chart.maintenanceCosts")}
				</span>
				<span className="legend-item">
					<span className="legend-color legend-restoration" />
					{t("building:chart.restorationCosts")}
				</span>
			</div>
		</div>
	);
}

interface ZNScatterCellProps {
	condition: number | undefined;
}

/**
 * Z/N scatter chart cell - horizontal axis from 100 (left) to 50 (right).
 * - 7mm left padding
 * - 5 segments × 10mm = 50mm for Z/N range 100→50 (ticks only on 10s)
 * - Dot positioned by Z/N value with condition color
 */
function ZNScatterCell({ condition }: ZNScatterCellProps) {
	const conditionColor = getConditionColor(condition);

	// Calculate dot position: 100 is at 0%, 50 is at 100% of the scale
	// Formula: (100 - znValue) / 50 * 100 = percentage from left
	const dotPosition = condition !== undefined ? ((100 - condition) / 50) * 100 : 50;
	// Clamp between 0 and 100
	const clampedPosition = Math.max(0, Math.min(100, dotPosition));

	return (
		<div className="summary-cell cell-zn-scatter">
			<div className="zn-scatter-axis">
				<div className="zn-scatter-padding" />
				<div className="zn-scatter-scale">
					{/* Background color zones: 100-85 dark green, 85-70 light green, 70-50 orange */}
					<div className="zn-scatter-background">
						<div className="zn-bg-zone zn-bg-good" />
						<div className="zn-bg-zone zn-bg-ok" />
						<div className="zn-bg-zone zn-bg-bad" />
					</div>
					{/* Tick marks - only on 10s (5 segments for 100→50) */}
					<div className="zn-scatter-ticks">
						{ZN_AXIS_LABELS.slice(0, -1).map((_, i) => (
							<div key={i} className="zn-tick" />
						))}
					</div>
					{/* Condition dot */}
					{condition !== undefined && (
						<div
							className="zn-scatter-dot"
							style={{
								left: `${clampedPosition}%`,
								backgroundColor: conditionColor,
							}}
						/>
					)}
				</div>
			</div>
		</div>
	);
}

/**
 * Footer cell for Z/N axis labels (100, 90, 80, 70, 60, 50) at bottom of scatter chart.
 */
function ZNAxisFooterCell() {
	return (
		<div className="summary-cell cell-zn-scatter cell-zn-axis-footer">
			<div className="zn-scatter-axis">
				<div className="zn-scatter-padding" />
				<div className="zn-scatter-scale">
					<div className="zn-scatter-labels">
						{ZN_AXIS_LABELS.map((label) => (
							<span key={label} className="zn-axis-label">
								{label}
							</span>
						))}
					</div>
				</div>
			</div>
		</div>
	);
}

interface TotalRowProps {
	totalCondition: number;
}

/**
 * Total row - only spans left portion (name through Z/N value), no timeline cells.
 * Columns: name | weight | % | Z/N scatter | Z/N value
 */
function TotalRow({ totalCondition }: TotalRowProps) {
	const { t } = useTranslation();

	return (
		<div className="summary-row summary-row-total">
			{/* Element name */}
			<div className="summary-cell cell-name">{t("building:label.total")}</div>

			{/* Weight bar - empty for total */}
			<div className="summary-cell cell-weight-bar" />

			{/* Percentage - empty for total */}
			<div className="summary-cell cell-percent" />

			{/* Z/N scatter chart cell */}
			<ZNScatterCell condition={totalCondition} />

			{/* Z/N value */}
			<div className="summary-cell cell-zn">{totalCondition}</div>
		</div>
	);
}

interface ElementRowProps {
	element: BuildingElement;
	maxWeight: number;
	years: number[];
	isFirst?: boolean;
}

/**
 * Single element row in the unified grid (covers both element data and timeline).
 * Columns: name | weight | % | Z/N scatter | Z/N value | gap | years...
 */
function ElementRow({ element, maxWeight, years, isFirst }: ElementRowProps) {
	const condition = element.condition;
	const weightPercent = element.weight ?? 0;
	const weightBarWidth = maxWeight > 0 ? (weightPercent / maxWeight) * 100 : 0;

	return (
		<div className={`summary-row${isFirst ? " summary-row-first" : ""}`}>
			{/* Element name */}
			<div className="summary-cell cell-name">{element.buildingPart?.name || "-"}</div>

			{/* Weight bar */}
			<div className="summary-cell cell-weight-bar">
				<div className="weight-bar" style={{ width: `${weightBarWidth}%` }} />
			</div>

			{/* Percentage */}
			<div className="summary-cell cell-percent">{formatPercent(weightPercent)}</div>

			{/* Z/N scatter chart cell */}
			<ZNScatterCell condition={condition} />

			{/* Z/N value - now directly after scatter chart */}
			<div className="summary-cell cell-zn">{condition ?? "-"}</div>

			{/* Gap between left panel and timeline */}
			<div className="summary-cell cell-gap" />

			{/* Year cells (timeline) */}
			{years.map((year) => {
				const isRestoration = element.restorationYear === year;
				return (
					<div key={year} className="summary-cell cell-year">
						{isRestoration && <span className="restoration-dot" />}
					</div>
				);
			})}
		</div>
	);
}

/**
 * Footer row with Z/N axis labels and year labels at bottom.
 */
function FooterRow({ years }: { years: number[] }) {
	return (
		<div className="summary-row summary-row-footer">
			{/* Empty name cell */}
			<div className="summary-cell cell-name" />

			{/* Empty weight bar cell */}
			<div className="summary-cell cell-weight-bar" />

			{/* Empty percent cell */}
			<div className="summary-cell cell-percent" />

			{/* Z/N axis labels (100, 90, 80, 70, 60, 50) */}
			<ZNAxisFooterCell />

			{/* Empty Z/N value cell */}
			<div className="summary-cell cell-zn" />

			{/* Gap */}
			<div className="summary-cell cell-gap" />

			{/* Year labels at bottom */}
			{years.map((year) => (
				<div key={year} className="summary-cell cell-year cell-year-footer">
					<span className="year-label">{year}</span>
				</div>
			))}
		</div>
	);
}

// =============================================================================
// MAIN COMPONENT
// =============================================================================

/**
 * Summary Page / One-Pager (Tables 5, 6, 7 and Shape 9 in Aspose)
 * Compact overview with:
 * - Basic data table (table 6)
 * - Evaluation parameters table (table 7)
 * - Element overview with weight bars and Z/N scatter chart (table 5)
 * - Condensed cost chart using Recharts (shape 9)
 *
 * Uses fixed mm-based positioning for pixel-perfect alignment on A4 landscape:
 * - Upper-right cost chart and lower-right timeline (shared year columns)
 * - Lower-left element table and lower-right timeline (shared rows)
 */
export function SummaryPage({ building, projection, inflationRate = 2 }: SummaryPageProps) {
	const { t } = useTranslation();

	// Data extraction
	const elements = building.currentRating?.elements || [];
	const validElements = elements.filter((e) => e.weight && e.weight > 0);
	const maxWeight = Math.max(...validElements.map((e) => e.weight || 0), 1);
	const periods = projection.periodList;

	// Z/N ratio calculation
	const znRatio = calculateZNRatio(elements);
	const firstPeriod = periods[0];
	const timeValue = firstPeriod ? Math.round(firstPeriod.timeValue) : 0;

	// Generate years array
	const years = Array.from({ length: YEAR_COUNT }, (_, i) => projection.startYear + i);

	// Cost data in kCHF for Recharts
	const chartData: CostChartData[] = periods.slice(0, YEAR_COUNT).map((p, i) => ({
		year: years[i],
		maintenance: Math.round(p.maintenanceCosts / 1000),
		restoration: Math.round(
			p.restorationElements.reduce((sum, re) => sum + re.restorationCosts, 0) / 1000
		),
	}));

	return (
		<div className="report-page report-page-content summary-page page-break">
			<ReportHeader building={building} />

			<div className="report-page-body summary-body">
				{/* Full-height year column grid overlay for alignment verification */}
				<div className="summary-year-grid-overlay">
					{years.map((year) => (
						<div key={year} className="summary-year-grid-line" />
					))}
				</div>

				{/* ============================================================
				    SECTION HEADERS: "Auf einen Blick" and Chart title
				    ============================================================ */}
				<div className="summary-header-row">
					<div className="section-header section-header-left">{t("building:report.atAGlance")}</div>
					<div className="section-header section-header-right">
						{t("building:report.costChartTitle")}
					</div>
				</div>

				{/* ============================================================
				    UPPER SECTION: Parameter tables + Cost chart
				    ============================================================ */}
				<div className="summary-upper">
					{/* Left: Parameter tables */}
					<div className="summary-params">
						{/* Grunddaten */}
						<div className="params-section">
							<div className="params-header">{t("building:report.basicData")}</div>
							<table className="params-table">
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
										<th>
											{t("building:report.insuredValue")}
											{building.insuredValueYear && ` (${building.insuredValueYear})`}
										</th>
										<td>{formatCHF(building.insuredValue)}</td>
									</tr>
								</tbody>
							</table>
						</div>

						{/* Auswertung */}
						<div className="params-section">
							<div className="params-header">{t("building:report.evaluationParams")}</div>
							<table className="params-table">
								<tbody>
									<tr>
										<th>
											{t("building:report.duration")}; {t("building:report.inflation")}
										</th>
										<td>
											{projection.duration} {t("building:report.years")};{" "}
											{formatPercent(inflationRate)}
										</td>
									</tr>
									<tr>
										<th>
											{t("building:report.timeValue")} ({t("building:report.znRatio")}: {znRatio})
										</th>
										<td>{formatCHF(timeValue)}</td>
									</tr>
									<tr>
										<th>{t("building:report.shortTermCosts")}</th>
										<td>{formatCHF(getShortTermCosts(periods))}</td>
									</tr>
									<tr>
										<th>{t("building:report.midTermCosts")}</th>
										<td>{formatCHF(getMidTermCosts(periods))}</td>
									</tr>
									<tr>
										<th>{t("building:report.longTermCosts")}</th>
										<td>{formatCHF(getLongTermCosts(periods))}</td>
									</tr>
									<tr>
										<th>{t("building:report.avgMaintenanceCosts")}</th>
										<td>{formatCHF(getAverageMaintenanceCosts(periods))}</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>

					{/* Right: Cost chart (Recharts) */}
					<div className="summary-chart-container">
						<SummaryCostChart data={chartData} />
					</div>
				</div>

				{/* ============================================================
				    LOWER SECTION: Unified grid (Element table + Timeline)
				    Columns: name | weight | % | Z/N scatter | Z/N value | gap | years...
				    ============================================================ */}
				<div className="summary-lower">
					{/* Unified grid */}
					<div
						className="summary-grid"
						style={{ "--year-count": YEAR_COUNT } as React.CSSProperties}
					>
						{/* Label header row - "Anteil" and "Z/N Wert" */}
						<div className="summary-row summary-row-label-header">
							{/* Empty name cell */}
							<div className="summary-cell cell-name" />
							{/* "Anteil" spans weight + percent columns */}
							<div className="summary-cell cell-anteil-header">Anteil</div>
							{/* "Z/N Wert" spans scatter + Z/N value columns */}
							<div className="summary-cell cell-zn-wert-header">Z/N Wert</div>
						</div>

						{/* Total row - only left portion, no timeline */}
						<TotalRow totalCondition={znRatio} />

						{/* Element rows with timeline */}
						{validElements.map((element, index) => (
							<ElementRow
								key={element.id}
								element={element}
								maxWeight={maxWeight}
								years={years}
								isFirst={index === 0}
							/>
						))}

						{/* Footer row with axis labels and year labels */}
						<FooterRow years={years} />
					</div>

					{/* Grid footer labels */}
					<div className="summary-grid-footer">
						{/* <span className="footer-left-labels">
							<span className="footer-label">Baustruktur</span>
							<span className="footer-label">Baulicher Zustand</span>
						</span> */}
						<span className="footer-structure-label">Baustruktur</span>
						<span className="footer-condition-label">Baulicher Zustand</span>
						<span className="footer-timeline-label">Instandsetzungszeitpunkte</span>
					</div>
				</div>
			</div>

			<ReportFooter pageNumber={7} />
		</div>
	);
}
