import { Button, Card } from "@salesforce/design-system-react";
import { Col, Grid, Row } from "@zeitwert/ui-slds/common/Grid";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface Renovation {
	id: string;
	buildingId: string;
	buildingName: string;
	part: string;
	costs: string;
	dueDate: string;
}

@inject("appStore", "session")
@observer
export default class HomeCardRenovationList extends React.Component {

	@observable renovationList: Renovation[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadRenovationList();
	}

	render() {
		return (
			<Card
				heading={<b>{"Upcoming Renovations"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none  slds-p-around_small slds-card__body_with_header_footer"
				footer={<Button>Show more</Button>}
			>
				<Grid>
					<Row nowrap>
						<Col totalCols={10} cols={6}>
							<div>
								<h2>Total</h2>
							</div>
						</Col>
						<Col totalCols={10} cols={5}>
							<div className="slds-clearfix">
								<div className="slds-float_right">
									<h2>&nbsp;</h2>
								</div>
							</div>
							<div className="slds-clearfix">
								<div className="slds-float_right">
									<p className="slds-text-heading_large">1'629 kCHF</p>
								</div>
							</div>
						</Col>
					</Row>
					<Row nowrap>
						&nbsp;
					</Row>
					<Row nowrap>
						&nbsp;
					</Row>
				</Grid>
				{!this.renovationList.length && <p className="slds-m-horizontal_medium">No renovation items.</p>}
				{
					this.renovationList.length &&
					<div>
						{
							this.renovationList.map((todo: Renovation, index: number) => (
								<article className="slds-tile" key={"renovation-" + index}>
									<h3 className="slds-tile__title slds-truncate">
										<div className="slds-clearfix">
											<span>{todo.part} ({todo.buildingId})</span>
											<div className="slds-float_right">
												<p>{todo.costs}</p>
											</div>
										</div>
									</h3>
									<div className="slds-tile__detail">
										<a href="/#">{todo.buildingName}</a>
									</div>
								</article>
							))
						}
					</div>
				}
			</Card>
		);
	}

	private loadRenovationList() {
		this.renovationList = [
			{
				id: "1",
				buildingId: "024-7552",
				buildingName: "Musterstrasse 11, 1234 Musterstadt",
				part: "Fenster",
				costs: "60 kCHF",
				dueDate: "2021"
			},
			{
				id: "2",
				buildingId: "024-9748",
				buildingName: "Musterstrasse 12, 1234 Musterstadt",
				part: "Dach",
				costs: "130 kCHF",
				dueDate: "2021"
			},
			{
				id: "3",
				buildingId: "048-362",
				buildingName: "Musterstrasse 14, 1234 Musterstadt",
				part: "Heizung",
				costs: "30 kCHF",
				dueDate: "2021"
			},
		];
	}

}

