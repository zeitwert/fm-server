import { Button, ButtonGroup, Card, MediaObject, Spinner } from "@salesforce/design-system-react";
import { ItemListModel } from "@zeitwert/ui-model";
import {
	DataTableCellForTemperature,
	DataTableCellWithChannelIcon,
	DataTableCellWithEntityIcon,
	DataTableCellWithLink
} from "@zeitwert/ui-slds/custom/CustomDataTableCells";
import { ReportViewer } from "@zeitwert/ui-slds/report/ReportViewer";
import { AppCtx } from "App";
import classNames from "classnames";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

const TEMPERATURE_TRESHOLD = 25;

enum SelectionType {
	ALL = "all",
	HOT = "hot",
	COLD = "cold"
}

const DOC_WITH_STATISTICS_DATAMART = "doc.docsWithStatistics";

interface HomeActiveBusinessProcessesProps { }

@inject("appStore", "logger", "showToast")
@observer
export default class HomeActiveBusinessProcesses extends React.Component<HomeActiveBusinessProcessesProps> {
	@observable selectionType = SelectionType.ALL;
	@observable docListStore = ItemListModel.create({ datamart: DOC_WITH_STATISTICS_DATAMART });

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: HomeActiveBusinessProcessesProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		try {
			await this.docListStore.executeTemplate(DOC_WITH_STATISTICS_DATAMART + ".by-temperature");
		} catch (error: any) {
			this.ctx.logger.error("Could not load doc list", error);
			this.ctx.showToast("warning", "Could not load doc list");
		}
	}

	render() {
		return (
			<Card
				header={
					<MediaObject
						body={
							<div className="slds-text-align_right">
								<ButtonGroup>
									<Button
										onClick={() => (this.selectionType = SelectionType.ALL)}
										variant={this.selectionType === SelectionType.ALL ? "brand" : "neutral"}
									>
										All
									</Button>
									<Button
										onClick={() => (this.selectionType = SelectionType.COLD)}
										variant={this.selectionType === SelectionType.COLD ? "brand" : "neutral"}
									>
										Cold
									</Button>
									<Button
										onClick={() => (this.selectionType = SelectionType.HOT)}
										variant={this.selectionType === SelectionType.HOT ? "brand" : "neutral"}
									>
										Hot
									</Button>
								</ButtonGroup>
							</div>
						}
						figure={
							<div className="slds-text-heading_small slds-truncate slds-m-right_xx-small">
								{"Running Business Processes (" +
									(this.docListStore.reportData?.data.length || 0) +
									")"}
							</div>
						}
						verticalCenter
					/>
				}
				heading=""
				className="fa-height-100 slds-scrollable_none"
				bodyClassName="slds-card__body_inner"
			>
				{(!this.docListStore.reportData || !this.docListStore.reportData) && (
					<Spinner variant="brand" size="large" />
				)}
				{this.docListStore.reportData &&
					this.selectionType === SelectionType.ALL &&
					this.renderReportViewer(this.selectionType)}
				{this.docListStore.reportData &&
					this.selectionType === SelectionType.HOT &&
					this.renderReportViewer(this.selectionType)}
				{this.docListStore.reportData &&
					this.selectionType === SelectionType.COLD &&
					this.renderReportViewer(this.selectionType)}
			</Card>
		);
	}

	renderReportViewer(type: SelectionType) {
		let data = undefined;
		switch (type) {
			default:
			case SelectionType.ALL:
				data = this.docListStore.reportData.data
					.slice()
					.sort((d1: any, d2: any) => (d1.temperature < d2.temperature ? 1 : -1));
				break;
			case SelectionType.HOT:
				data = this.docListStore.reportData?.data
					?.filter((d: any) => d.temperature >= TEMPERATURE_TRESHOLD)
					.slice()
					.sort((d1: any, d2: any) => (d1.temperature < d2.temperature ? 1 : -1));
				break;
			case SelectionType.COLD:
				data = this.docListStore.reportData?.data
					.filter((d: any) => d.temperature < TEMPERATURE_TRESHOLD)
					.slice()
					.sort((d1: any, d2: any) => (d1.temperature > d2.temperature ? 1 : -1));
				break;
		}
		const final = {
			data: data,
			header: this.docListStore.reportData.header,
			layoutType: this.docListStore.reportData.layoutType
		};
		const classes = classNames("slds-m-bottom_large fa-overflow", "running-processes");
		return (
			<div className={classes}>
				{this.docListStore.template?.layout && (
					<ReportViewer
						layout={this.docListStore.template.layout}
						data={final}
						templates={{
							channelIcon: DataTableCellWithChannelIcon,
							temperature: DataTableCellForTemperature,
							link: DataTableCellWithLink,
							entityIcon: DataTableCellWithEntityIcon
						}}
						options={{
							isJoined: false
						}}
					/>
				)}
				{!this.docListStore.template?.layout && (
					<p className="slds-p-vertical_x-small">No hot Business Processes.</p>
				)}
			</div>
		);
	}
}
