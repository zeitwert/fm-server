
import { Card, Icon } from "@salesforce/design-system-react";
import { API, Config, session } from "@zeitwert/ui-model";
import { Col, Grid, Row } from "@zeitwert/ui-slds/common/Grid";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface Overview {
	accountId: number;
	accountName: string;
	buildingCount: number;
	portfolioCount: number;
	insuranceValue: number;
	timeValue: number;
	shortTermRenovationCosts: number;
	midTermRenovationCosts: number;
	ratingCount: number;
}

@inject("appStore", "session")
@observer
export default class HomeCardOverview extends React.Component {

	@observable overview: Overview | undefined;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadOverview();
	}

	render() {
		const accountImageUrl = Config.getRestUrl("account", `accounts/${this.overview?.accountId}/logo`);
		return (
			<Card
				icon={<Icon category="standard" name="account" size="small" />}
				heading={<b>{this.overview?.accountName}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-around_small slds-card__body_with_header_footer"
			>
				<Grid>
					<Row nowrap>
						<Col totalCols={12} cols={4}>
						</Col>
						<Col totalCols={12} cols={4}>
							<img src={accountImageUrl} alt="Account flag" />
						</Col>
						<Col totalCols={12} cols={4}>
						</Col>
					</Row>
					<Row nowrap className="slds-p-top_small">
						<Col totalCols={12} cols={12}>
							<Grid>
								{this.fact(this.overview?.buildingCount, "Immobilie", "Immobilien", "/building")}
								{this.fact(this.overview?.portfolioCount, "Portfolio", "Portfolios", "/portfolio")}
								{this.fact(this.overview?.ratingCount, "Bewertung", "Bewertungen")}
								{this.fact(this.overview?.insuranceValue, "kCHF Versicherungswert")}
								{this.fact(this.overview?.timeValue, "kCHF Zeitwert")}
								{this.fact(this.overview?.shortTermRenovationCosts, "kCHF IS kurzfristig")}
								{this.fact(this.overview?.midTermRenovationCosts, "kCHF IS mittelfristig")}
							</Grid>
						</Col>
					</Row>
				</Grid>
			</Card>
		);
	}

	private async loadOverview() {
		const rsp = await API.get(Config.getRestUrl("home", "overview/" + session.sessionInfo?.account.id))
		this.overview = rsp.data;
	}

	private fact = (nr?: number, name?: string, multiName?: string, url?: string) => {
		if (nr === undefined || nr === null) {
			return <></>;
		}
		return <Row nowrap>
			<Col totalCols={12} cols={4}>
				<div className="slds-clearfix">
					<div className="slds-float_right">
						<p className="slds-text-heading_large"><b>{session.formatter.formatValue(nr, 0)}</b></p>
					</div>
				</div>
			</Col>
			<Col totalCols={12} cols={8} className="slds-p-left_x-small">
				<p className="slds-text-heading_medium slds-p-top_x-small slds-truncate">
					{
						!url && (nr === 1 ? name : multiName || name)
					}
					{
						!!url && <a href={url}>{nr === 1 ? name : multiName || name}</a>
					}
				</p>
			</Col>
		</Row>;
	}

}
