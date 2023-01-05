
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { observer } from "mobx-react";
import React from "react";
import { ProjectionResult } from "../../@zeitwert/ui-model/fm/building/model/ProjectionResultDto";

export interface TabProjectionChartProps {
	projection: ProjectionResult;
}

@observer
export default class TabProjectionChart extends React.Component<TabProjectionChartProps> {

	render() {
		const { projection } = this.props;
		const timeValues = projection.periodList;
		const years = timeValues.map((tv) => tv.year.toString());
		const valueOptions = Object.assign({}, ValueOptions);
		const costOptions = Object.assign({}, CostOptions);
		valueOptions.plotOptions.area.pointStart = !!timeValues?.length ? timeValues[0].year : 0;
		valueOptions.xAxis.categories = years;
		valueOptions.series[0].data = timeValues.map((tv) => Math.round(tv.originalValue));
		valueOptions.series[1].data = timeValues.map((tv) => Math.round(tv.timeValue));
		costOptions.plotOptions.area.pointStart = !!timeValues?.length ? timeValues[0].year : 0;
		costOptions.xAxis.categories = years;
		costOptions.series = [costOptions.series[0]];
		costOptions.series[0].data = timeValues.map((tv) => Math.round(tv.maintenanceCosts));
		projection.elementList.forEach((element, index) => {
			costOptions.series[index + 1] = {
				name: element.element.name,
				showInLegend: false,
				data: [],
				color: "#a0cd5f",
				stack: "restoration",
				tooltip: {
					pointFormat: "<tspan style='fill:#a0cd5f'>●</tspan> <b>" + element.buildingPart.name + "</b> (" + element.building.name + "): <b>CHF {point.y:,.0f}</b>"
				},
			} as any;
			costOptions.series[index + 1].data = timeValues.map((tv) => tv.restorationElements.filter(re => re.element.id === element.element.id)?.[0]?.restorationCosts || 0);
		});
		return (
			<div style={{ height: "100%", width: "100%" }}>
				<HighchartsReact highcharts={Highcharts} options={valueOptions} containerProps={{ style: { height: "30%", width: "100%" } }} />
				<HighchartsReact highcharts={Highcharts} options={costOptions} containerProps={{ style: { height: "70%", width: "100%" } }} />
			</div>
		);
	}

}

const ValueOptions = {
	chart: {
		type: "line"
	},
	title: {
		text: ""
	},
	credits: {
		enabled: false
	},
	xAxis: {
		categories: [] as string[]
	},
	yAxis: {
		title: {
			text: "Gebäudewert (CHF)"
		},
	},
	tooltip: {
		headerFormat: "<b>Jahr {point.x}</b><br />",
		valuePrefix: "CHF ",
		shared: true,
		split: false,
		enabled: true
	},
	plotOptions: {
		area: {
			pointStart: 45,
			marker: {
				enabled: false,
				symbol: "circle",
				radius: 2,
				states: {
					hover: {
						enabled: true
					}
				}
			}
		},
	},
	series: [
		{
			name: "Neuwert",
			data: [] as number[],
			color: "#000000",
		},
		{
			name: "Zeitwert",
			data: [] as number[],
			color: "#50aae1",
		},
	]
};

const CostOptions = {
	chart: {
		type: "column"
	},
	title: {
		text: ""
	},
	credits: {
		enabled: false
	},
	xAxis: {
		categories: [] as string[]
	},
	yAxis: {
		min: 0,
		allowDecimals: false,
		title: {
			text: "Kosten (CHF)"
		},
		xstackLabels: {
			enabled: true,
			style: {
				fontWeight: "bold",
				color: Highcharts.defaultOptions.title?.style?.color || "gray"
			}
		}
	},
	tooltip: {
		headerFormat: "<b>Jahr {point.x}</b><br />",
		pointFormat: "{series.name}: {point.y}<br/>Total: {point.stackTotal}",
		valuePrefix: "CHF ",
	},
	plotOptions: {
		column: {
			stacking: "normal"
		},
		area: {
			pointStart: 45,
			marker: {
				enabled: false,
				symbol: "circle",
				radius: 2,
				states: {
					hover: {
						enabled: true
					}
				}
			}
		},
	},
	series: [
		{
			name: "Instandhaltung",
			showInLegend: false,
			data: [] as number[],
			color: "#4b873c",
			stack: "maintenance",
			tooltip: {
				pointFormat: "<tspan style='fill:#a0cd5f'>●</tspan> <b>Instandhaltung</b> (alle Gebäude): <b>CHF {point.y:,.0f}</b>"
			},
		},
	]
};
