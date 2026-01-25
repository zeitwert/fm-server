import {
	ResponsiveContainer,
	LineChart,
	Line,
	XAxis,
	YAxis,
	Legend,
	CartesianGrid,
} from "recharts";
import { useTranslation } from "react-i18next";
import type { ProjectionPeriod } from "../../../../types";
import { COLORS, formatCurrency } from "../../../components/projectionChartConfig";

interface PrintValueChartProps {
	data: ProjectionPeriod[];
	containerWidth?: number;
}

interface ChartDataPoint {
	year: number;
	originalValue: number;
	timeValue: number;
}

function transformData(periods: ProjectionPeriod[]): ChartDataPoint[] {
	return periods.map((period) => ({
		year: period.year,
		originalValue: Math.round(period.originalValue),
		timeValue: Math.round(period.timeValue),
	}));
}

// Print margins - minimal, no space for Y-axis label
const PRINT_MARGIN = { top: 5, right: 10, left: 0, bottom: 5 };
const PRINT_Y_AXIS_WIDTH = 70;
const PRINT_SYNC_ID = "print-projection";

/**
 * Print-optimized value chart for reports.
 * - No Y-axis label
 * - Smaller fonts
 * - Compact legend at bottom
 */
export function PrintValueChart({ data, containerWidth = 0 }: PrintValueChartProps) {
	const { t } = useTranslation();
	const chartData = transformData(data);

	// Calculate X-axis padding to align with bar chart
	const chartAreaWidth = containerWidth - PRINT_MARGIN.left - PRINT_MARGIN.right - PRINT_Y_AXIS_WIDTH;
	const numberOfYears = chartData.length;
	const xAxisPadding =
		containerWidth > 0 && numberOfYears > 0 ? Math.round(chartAreaWidth / numberOfYears / 2) : 15;

	return (
		<ResponsiveContainer width="100%" height="100%">
			<LineChart data={chartData} syncId={PRINT_SYNC_ID} margin={PRINT_MARGIN}>
				<CartesianGrid strokeDasharray="3 3" vertical={false} />
				<XAxis
					dataKey="year"
					padding={{ left: xAxisPadding, right: xAxisPadding }}
					tick={{ fontSize: 9 }}
					tickLine={{ stroke: "#ccc" }}
				/>
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
				<Line
					type="linear"
					dataKey="originalValue"
					name={t("building:chart.originalValueIndexed")}
					stroke={COLORS.originalValue}
					dot={{ r: 2 }}
					strokeWidth={1.5}
				/>
				<Line
					type="linear"
					dataKey="timeValue"
					name={t("building:chart.timeValue")}
					stroke={COLORS.timeValue}
					dot={{ r: 2 }}
					strokeWidth={1.5}
				/>
			</LineChart>
		</ResponsiveContainer>
	);
}
