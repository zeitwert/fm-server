import Button from "@salesforce/design-system-react/components/button";
import Card from "@salesforce/design-system-react/components/card";
import { EntityType } from "@zeitwert/ui-model";
import { Timeline } from "@zeitwert/ui-slds/timeline/Timeline";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import HomeCardRecentActivity, { Activity } from "./HomeCardRecentActivity";

@inject("appStore", "session")
@observer
export default class HomeCardRecentActivityList extends React.Component {

	@observable activityList: Activity[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadActivityList();
	}

	render() {
		return (
			<Card
				heading={<b>{"Recent Activity (" + this.activityList.length + ")"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_small slds-card__body_with_header_footer"
				footer={<Button>Show more</Button>}
			>
				{!this.activityList.length && <p className="slds-m-horizontal_medium">No recent activity records yet.</p>}
				{this.activityList.length &&
					<>
						<Timeline>
							{this.activityList.map((activity: Activity, index: number) => (
								<HomeCardRecentActivity
									key={index}
									activity={activity}
									onClick={() => null}
									isExpanded={false}
								/>
							))}
						</Timeline>
					</>
				}
			</Card >
		);
	}

	private loadActivityList() {
		this.activityList = [
			{
				id: "1",
				building: {
					id: "1362-123-14",
					name: "Bürogebäude Verwaltung",
					image: "tbd"
				},
				type: {
					type: EntityType.BUILDING,
					iconCategory: "standard",
					iconName: "store",
				},
				action: "Bauteil Wärmeerzeugung geändert",
				user: {
					id: "one",
					name: "Martin Rüegg"
				},
				date: "gestern"
			},
			{
				id: "2",
				building: {
					id: "2300 1208",
					name: "Geschäftshaus mit Saal",
					image: "tbd"
				},
				type: {
					type: EntityType.BUILDING,
					iconCategory: "standard",
					iconName: "store",
				},
				action: "Bauteil Fenster geändert",
				user: {
					id: "one",
					name: "Peter Ziegler"
				},
				date: "12.09.2020"
			},
			{
				id: "3",
				building: {
					id: "00-27-4872-5",
					name: "Neufeld",
					image: "tbd"
				},
				type: {
					type: EntityType.BUILDING,
					iconCategory: "standard",
					iconName: "store",
				},
				action: "Stammdaten geändert",
				user: {
					id: "one",
					name: "Anna Muster"
				},
				date: "04.08.2020"
			},
		];
	}

}
