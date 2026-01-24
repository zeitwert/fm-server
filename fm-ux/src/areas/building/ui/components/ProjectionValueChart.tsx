import {
	ResponsiveContainer,
	LineChart,
	Line,
	XAxis,
	YAxis,
	Tooltip,
	Legend,
	CartesianGrid,
} from "recharts";
import { useTranslation } from "react-i18next";
import type { ProjectionPeriod } from "../../types";
import {
	CHART_MARGIN,
	Y_AXIS_WIDTH,
	SYNC_ID,
	COLORS,
	formatCurrency,
} from "./projectionChartConfig";

interface ProjectionValueChartProps {
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

export function ProjectionValueChart({ data, containerWidth = 0 }: ProjectionValueChartProps) {
	const { t } = useTranslation();
	const chartData = transformData(data);

	// Calculate dynamic X-axis padding to align with BarChart's implicit bar padding
	// The BarChart centers bars in bands, leaving ~half a band width on each side
	// We replicate this for the LineChart by calculating: (chartAreaWidth / numberOfYears) / 2
	const chartAreaWidth = containerWidth - CHART_MARGIN.left - CHART_MARGIN.right - Y_AXIS_WIDTH;
	const numberOfYears = chartData.length;
	const xAxisPadding =
		containerWidth > 0 && numberOfYears > 0 ? Math.round(chartAreaWidth / numberOfYears / 2) : 20;

	return (
		<ResponsiveContainer width="100%" height="100%">
			<LineChart data={chartData} syncId={SYNC_ID} margin={CHART_MARGIN}>
				<CartesianGrid strokeDasharray="3 3" vertical={false} />
				<XAxis dataKey="year" padding={{ left: xAxisPadding, right: xAxisPadding }} />
				<YAxis
					width={Y_AXIS_WIDTH}
					tickFormatter={formatCurrency}
					label={{
						value: t("building:chart.valueAxisLabel"),
						angle: -90,
						position: "insideLeft",
						style: { textAnchor: "middle" },
					}}
				/>
				<Tooltip
					formatter={(value, name) => [`CHF ${formatCurrency(value as number)}`, name]}
					labelFormatter={(label) => `${t("building:chart.year")} ${label}`}
				/>
				<Legend />
				<Line
					type="linear"
					dataKey="originalValue"
					name={t("building:chart.originalValue")}
					stroke={COLORS.originalValue}
					dot={{ r: 3 }}
					strokeWidth={2}
				/>
				<Line
					type="linear"
					dataKey="timeValue"
					name={t("building:chart.timeValue")}
					stroke={COLORS.timeValue}
					dot={{ r: 3 }}
					strokeWidth={2}
				/>
			</LineChart>
		</ResponsiveContainer>
	);
}
