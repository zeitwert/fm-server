
import { API, Config } from "@comunas/ui-model";
import { AppCtx } from "App";
import { EMPTY_RESULT, ProjectionResult } from "building/ui/ProjectionResult";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import TabProjectionChart from "./TabProjectionChart";
import TabProjectionTable from "./TabProjectionTable";

export interface TabProjectionProps {
	url: string;
}

enum ReportType {
	CHART, TABLE
}

@observer
export default class TabProjection extends React.Component<TabProjectionProps> {

	@observable
	isLoading: boolean = false;

	@observable
	loadNr: number = 0;

	@observable
	projection: ProjectionResult = EMPTY_RESULT;

	@observable
	reportType: ReportType = ReportType.CHART;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.loadProjection(this.props.url);
	}

	render() {
		if (this.isLoading) {
			return (
				<div className="slds-spinner_container">
					<div role="status" className="slds-spinner slds-spinner_medium slds-spinner_brand">
						<span className="slds-assistive-text">Loading</span>
						<div className="slds-spinner__dot-a"></div>
						<div className="slds-spinner__dot-b"></div>
					</div>
				</div>
			);
		}
		return (
			<div className="slds-vertical-tabs" style={{ height: "100%" }}>
				<ul className="slds-vertical-tabs__nav" role="tablist" aria-orientation="vertical" style={{ maxWidth: "3rem" }}>
					<li className={"slds-vertical-tabs__nav-item" + (this.reportType === ReportType.CHART ? " slds-is-active" : "")} title="Grafik" role="presentation">
						<a className="slds-vertical-tabs__link" href="/#" role="tab" tabIndex={0} id="rep-chart" onClick={(e) => { this.reportType = ReportType.CHART; e.stopPropagation(); e.preventDefault(); }}>
							<span className="slds-vertical-tabs__left-icon">
								<span className="slds-icon_container slds-icon-utility-chart slds-current-color">
									<svg className="slds-icon slds-icon_small" aria-hidden="true">
										<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#chart"></use>
									</svg>
								</span>
							</span>
							<span className="slds-truncate" title="Grafik">Grafik</span>
						</a>
					</li>
					<li className={"slds-vertical-tabs__nav-item" + (this.reportType === ReportType.TABLE ? " slds-is-active" : "")} title="Tabelle" role="presentation">
						<a className="slds-vertical-tabs__link" href="/#" role="tab" tabIndex={1} id="rep-table" onClick={(e) => { this.reportType = ReportType.TABLE; e.stopPropagation(); e.preventDefault(); }}>
							<span className="slds-vertical-tabs__left-icon">
								<span className="slds-icon_container slds-icon-utility-rows slds-current-color">
									<svg className="slds-icon slds-icon_small" aria-hidden="true">
										<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#rows"></use>
									</svg>
								</span>
							</span>
							<span className="slds-truncate" title="Tabelle">Tabelle</span>
						</a>
					</li>
				</ul>
				<div className="slds-vertical-tabs__content slds-show" id="rep-content" role="tabpanel">
					{this.reportType === ReportType.CHART && <TabProjectionChart projection={this.projection} key={"portf-chart-" + this.loadNr} />}
					{this.reportType === ReportType.TABLE && <TabProjectionTable projection={this.projection} key={"portf-table-" + this.loadNr} />}
				</div>
			</div>
		);
	}

	private loadProjection = async (url: string) => {
		this.isLoading = true;
		this.projection = await (await API.get(Config.getApiUrl("projection", url))).data;
		this.loadNr++;
		this.isLoading = false;
	};

}
