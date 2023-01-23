import { Card } from "@salesforce/design-system-react";
import { observer } from "mobx-react";
import React from "react";

@observer
export default class HomeCardCondition extends React.Component {

	render() {
		return (
			<Card
				heading={<b>{"Zustand 2021"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none"
			>
				<img className="slds-align_absolute-center" style={{ left: "5%", width: "90%", height: "90%" }} src="/demo/condition.png" alt="Under construction" />
				{/*<HighchartsReact highcharts={Highcharts} options={chartOptions} />*/}
			</Card>
		);
	}

}

// const chartOptions = {
// 	chart: {
// 		renderTo: 'container',
// 		defaultSeriesType: 'column'
// 	},
// 	xAxis: {
// 		categories: []
// 	},
// 	yAxis: {
// 	},
// 	legend: {
// 		layout: 'vertical',
// 		backgroundColor: '#FFFFFF',
// 		style: {
// 			left: '100px',
// 			top: '70px',
// 			bottom: 'auto'
// 		}
// 	},
// 	tooltip: {
// 		formatter: function (): any {
// 			const self = this as any;
// 			return '<b>' + self.series.name + '</b><br/>' +
// 				self.x + ': ' + self.y;
// 		}
// 	},
// 	plotOptions: {
// 		series: {
// 			stacking: 'normal'
// 		}
// 	},
// 	series: [{
// 		data: [29.9, null, null],
// 		pointWidth: 20

// 	}, {
// 		data: [null, 71.5, null],
// 		pointWidth: 40

// 	}, {
// 		data: [null, null, 106.4],
// 		pointWidth: 5

// 	}]
// };
