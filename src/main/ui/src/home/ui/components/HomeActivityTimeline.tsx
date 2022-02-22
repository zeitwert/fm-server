import { ActivityStore, ActivityStoreModel } from "@comunas/ui-model";
import Card from "@salesforce/design-system-react/components/card";
import ActivityPortletTimeline from "activity/timeline/ActivityPortletTimeline";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface HomeActivityTimelineProps { }

@inject("session")
@observer
export default class HomeActivityTimeline extends React.Component<HomeActivityTimelineProps> {
	@observable store: ActivityStore = ActivityStoreModel.create();

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: HomeActivityTimelineProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.store.loadActivitiesByUser(this.ctx.session.sessionInfo!.user);
	}

	render() {
		return (
			<Card
				heading="Activity Timeline"
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_small"
			>
				<ActivityPortletTimeline activities={this.store.activities} />
			</Card>
		);
	}
}
