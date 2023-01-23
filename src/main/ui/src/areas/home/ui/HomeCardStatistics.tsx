import { Card, Icon } from "@salesforce/design-system-react";
import { Col, Grid, Row } from "@zeitwert/ui-slds";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { inject, observer } from "mobx-react";
import React from "react";

@inject("appStore")
@observer
export default class HomeCardStatistics extends React.Component {

	render() {
		return (
			<Card
				icon={<Icon category="standard" name="metrics" size="small" />}
				heading={<b>{"Auswertung des Tages"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-around_small"
			>
				<Grid className="fa-height-100">
					<Row nowrap className="slds-m-left_small slds-m-right_small">
						<Col totalCols={12} cols={6}>
							<div>
								<h3>Gebäudezustand</h3>
							</div>
						</Col>
						<Col totalCols={12} cols={6}>
							<div className="slds-clearfix">
								<div className="slds-float_right">
									<h2>Z/N Portfolio</h2>
								</div>
							</div>
							<div className="slds-clearfix">
								<div className="slds-float_right">
									<p className="slds-text-heading_large">0.79</p>
								</div>
							</div>
						</Col>
					</Row>
					<Row nowrap>
						<Col totalCols={12} cols={12}>
							<HighchartsReact highcharts={Highcharts} options={chartOptions} />
						</Col>
					</Row>
				</Grid>
			</Card>
		);
	}

}

const chartOptions = {
	chart: {
		plotBackgroundColor: null,
		plotBorderWidth: null,
		plotShadow: false,
		type: "pie",
		height: 300
	},
	title: {
		text: ""
	},
	credits: {
		enabled: false
	},
	tooltip: {
		pointFormat: "{series.name}: <b>{point.percentage:.1f}%</b>"
	},
	accessibility: {
		point: {
			valueSuffix: "%"
		}
	},
	plotOptions: {
		pie: {
			allowPointSelect: true,
			cursor: "pointer",
			shadow: false,
			dataLabels: {
				enabled: false,
				format: "<b>{point.name}</b>: {point.percentage:.1f} %"
			},
			showInLegend: true
		}
	},
	series: [{
		name: "Condition",
		colorByPoint: true,
		size: "90%",
		innerSize: "70%",
		data: [{
			name: "gut",
			color: "green",
			y: 21
		}, {
			name: "mittel",
			color: "lightgreen",
			y: 53
		}, {
			name: "schlecht",
			color: "orange",
			y: 17
		}, {
			name: "sehr schlecht",
			color: "red",
			y: 9
		}]
	}]
};
