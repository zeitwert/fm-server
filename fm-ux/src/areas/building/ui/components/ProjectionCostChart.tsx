import {
	ResponsiveContainer,
	BarChart,
	Bar,
	XAxis,
	YAxis,
	Tooltip,
	Legend,
	CartesianGrid,
} from "recharts";
import { useTranslation } from "react-i18next";
import type { ProjectionPeriod, ProjectionElement } from "../../types";
import {
	CHART_MARGIN,
	Y_AXIS_WIDTH,
	SYNC_ID,
	COLORS,
	formatCurrency,
} from "./projectionChartConfig";

interface ProjectionCostChartProps {
	data: ProjectionPeriod[];
	elements: ProjectionElement[];
}

interface ChartDataPoint {
	year: number;
	maintenanceCosts: number;
	[key: string]: number; // Dynamic restoration element keys
}

function transformData(periods: ProjectionPeriod[]): ChartDataPoint[] {
	return periods.map((period) => {
		const dataPoint: ChartDataPoint = {
			year: period.year,
			maintenanceCosts: Math.round(period.maintenanceCosts),
		};

		// Add restoration costs for each element
		period.restorationElements.forEach((re) => {
			const key = `restoration_${re.element.id}`;
			dataPoint[key] = Math.round(re.restorationCosts);
		});

		return dataPoint;
	});
}

// Get unique element IDs from all periods
function getUniqueElementIds(periods: ProjectionPeriod[]): string[] {
	const elementIds = new Set<string>();
	periods.forEach((period) => {
		period.restorationElements.forEach((re) => {
			elementIds.add(re.element.id);
		});
	});
	return Array.from(elementIds);
}

// Create a lookup map for element names
function createElementNameMap(
	elements: ProjectionElement[]
): Map<string, { buildingPart: string; building: string }> {
	const map = new Map();
	elements.forEach((element) => {
		map.set(element.element.id, {
			buildingPart: element.buildingPart.name,
			building: element.building.name,
		});
	});
	return map;
}

export function ProjectionCostChart({ data, elements }: ProjectionCostChartProps) {
	const { t } = useTranslation();
	const chartData = transformData(data);
	const elementIds = getUniqueElementIds(data);
	const elementNameMap = createElementNameMap(elements);

	return (
		<ResponsiveContainer width="100%" height="100%">
			<BarChart data={chartData} syncId={SYNC_ID} margin={CHART_MARGIN}>
				<CartesianGrid strokeDasharray="3 3" vertical={false} />
				<XAxis dataKey="year" />
				<YAxis
					width={Y_AXIS_WIDTH}
					tickFormatter={formatCurrency}
					label={{
						value: t("building:chart.costAxisLabel"),
						angle: -90,
						position: "insideLeft",
						style: { textAnchor: "middle" },
					}}
				/>
				<Tooltip
					formatter={(value, name) => {
						const numValue = value as number;
						const strName = name as string;
						// Handle maintenance costs
						if (strName === t("building:chart.maintenanceCosts")) {
							return [`CHF ${formatCurrency(numValue)}`, strName];
						}
						// Handle restoration element costs
						const elementId = strName.replace("restoration_", "");
						const elementInfo = elementNameMap.get(elementId);
						if (elementInfo) {
							return [
								`CHF ${formatCurrency(numValue)}`,
								`${elementInfo.buildingPart} (${elementInfo.building})`,
							];
						}
						return [`CHF ${formatCurrency(numValue)}`, strName];
					}}
					labelFormatter={(label) => `${t("building:chart.year")} ${label}`}
				/>
				<Legend
					formatter={(value) => {
						if (value === t("building:chart.maintenanceCosts")) {
							return value;
						}
						// For restoration elements, show simplified label
						return t("building:chart.restorationCosts");
					}}
				/>
				{/* Maintenance costs bar - separate stack */}
				<Bar
					dataKey="maintenanceCosts"
					name={t("building:chart.maintenanceCosts")}
					stackId="maintenance"
					fill={COLORS.maintenanceCosts}
				/>
				{/* Restoration costs bars - stacked together */}
				{elementIds.map((elementId, index) => (
					<Bar
						key={elementId}
						dataKey={`restoration_${elementId}`}
						name={`restoration_${elementId}`}
						stackId="restoration"
						fill={COLORS.restorationCosts}
						stroke="#ffffff"
						strokeWidth={1}
						// Only show in legend for first restoration element
						legendType={index === 0 ? "square" : "none"}
					/>
				))}
			</BarChart>
		</ResponsiveContainer>
	);
}
