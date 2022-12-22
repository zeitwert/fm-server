import { Card, Icon } from "@salesforce/design-system-react";
import { Aggregate, DateFormat } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { AppCtx } from "frame/App";
import { computed, makeObservable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";

@inject("appStore", "session")
@observer
export default class HomeRecentItems extends React.Component {

	@computed get recentItems() {
		return [];
		// return this.ctx.appStore.recentItems
		// 	.slice()
		// 	.sort((ri1, ri2) =>
		// 		new Date(ri1.meta!.modifiedAt).getTime() < new Date(ri2.meta!.modifiedAt).getTime() ? 1 : -1
		// 	);
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	render() {
		return (
			<Card
				heading={"Recent Records (" + this.recentItems.length + ")"}
				className="fa-height-100"
				bodyClassName="slds-m-around_none"
			>
				{!this.recentItems.length && <p className="slds-m-horizontal_medium">No recent records yet.</p>}
				{this.recentItems.map((item: Aggregate) => (
					<Card
						key={item.id}
						heading={
							<Grid isVertical={false} className="slds-grid_vertical-align-center">
								<Col className="slds-size_8-of-12 slds-truncate">
									<Link to={"/" + item.type.type + "/" + item.id}>{item.caption}</Link>
									<div className="slds-text-body_small">
										{item.owner!.name} ⋅ {DateFormat.relativeTime(new Date(), item.meta!.modifiedAt)}
									</div>
								</Col>
								<Col className="slds-text-align_right">
									<div className="slds-text-body_small">
										{DateFormat.short(item.meta!.modifiedAt)}
									</div>
								</Col>
							</Grid>
						}
						icon={<Icon category={item.type.iconCategory} name={item.type.iconName} size="small" />}
					/>
				))}
			</Card>
		);
	}
}
