
import { Card, Icon } from "@salesforce/design-system-react";
import { API, Config, session } from "@zeitwert/ui-model";
import { Timeline } from "@zeitwert/ui-slds/timeline/Timeline";
import { AppCtx } from "frame/App";
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
				icon={<Icon category="standard" name="recent" size="small" />}
				heading={<b>{"Letzte Aktivitäten"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_small"
				footer={<></>}
			>
				{
					!this.activityList.length &&
					<p className="slds-m-horizontal_medium">Noch keine Aktivität.</p>
				}
				{
					!!this.activityList.length &&
					<Timeline>
						{
							this.activityList.map((activity: Activity, index: number) => (
								<HomeCardRecentActivity
									key={"ra-" + index}
									activity={activity}
									onClick={() => this.onClick(activity.objTypeId, activity.objId)}
									isExpanded={false}
								/>
							))
						}
					</Timeline>
				}
			</Card >
		);
	}

	private async loadActivityList() {
		const rsp = await API.get(Config.getRestUrl("home", "recentActivity/" + session.sessionInfo?.account?.id))
		this.activityList = rsp.data;
	}

	private onClick = (objTypeId: string, id: number) => {
		const type = objTypeId.substring(4);
		window.location.href = "/" + type + "/" + id;
	}

}
