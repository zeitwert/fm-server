import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Legend, CartesianGrid } from "recharts";
import { useTranslation } from "react-i18next";
import type { ProjectionPeriod } from "../../../../types";
import { COLORS, formatCurrency } from "../../../components/projectionChartConfig";

interface PrintCostChartProps {
	data: ProjectionPeriod[];
}

interface ChartDataPoint {
	year: number;
	maintenanceCosts: number;
	restorationCosts: number;
}

/**
 * Transform data - aggregate all restoration costs into single value for print
 */
function transformData(periods: ProjectionPeriod[]): ChartDataPoint[] {
	return periods.map((period) => {
		// Sum all restoration element costs
		const totalRestoration = period.restorationElements.reduce(
			(sum, re) => sum + re.restorationCosts,
			0
		);

		return {
			year: period.year,
			maintenanceCosts: Math.round(period.maintenanceCosts),
			restorationCosts: Math.round(totalRestoration),
		};
	});
}

// Print margins - minimal, no space for Y-axis label
const PRINT_MARGIN = { top: 5, right: 10, left: 0, bottom: 5 };
const PRINT_Y_AXIS_WIDTH = 70;
const PRINT_SYNC_ID = "print-projection";

/**
 * Print-optimized cost chart for reports.
 * - No Y-axis label
 * - Smaller fonts
 * - Compact legend at bottom
 * - Aggregated restoration costs (not per-element)
 */
export function PrintCostChart({ data }: PrintCostChartProps) {
	const { t } = useTranslation();
	const chartData = transformData(data);

	return (
		<ResponsiveContainer width="100%" height="100%">
			<BarChart data={chartData} syncId={PRINT_SYNC_ID} margin={PRINT_MARGIN}>
				<CartesianGrid strokeDasharray="3 3" vertical={false} />
				<XAxis dataKey="year" tick={{ fontSize: 9 }} tickLine={{ stroke: "#ccc" }} />
				<YAxis
					width={PRINT_Y_AXIS_WIDTH}
					tickFormatter={formatCurrency}
					tick={{ fontSize: 9 }}
					tickLine={{ stroke: "#ccc" }}
					axisLine={{ stroke: "#ccc" }}
				/>
				<Legend
					verticalAlign="bottom"
					height={20}
					iconSize={10}
					wrapperStyle={{ fontSize: 9, paddingTop: 5 }}
				/>
				{/* Maintenance costs bar */}
				<Bar
					dataKey="maintenanceCosts"
					name={t("building:chart.maintenanceCosts")}
					fill={COLORS.maintenanceCosts}
				/>
				{/* Restoration costs bar */}
				<Bar
					dataKey="restorationCosts"
					name={t("building:chart.restorationCosts")}
					fill={COLORS.restorationCosts}
				/>
			</BarChart>
		</ResponsiveContainer>
	);
}
