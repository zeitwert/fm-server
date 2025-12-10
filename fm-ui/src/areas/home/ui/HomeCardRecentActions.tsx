
import { Card, Icon } from "@salesforce/design-system-react";
import { Aggregate, API, Config, EntityTypes, Enumerated, session } from "@zeitwert/ui-model";
import { Timeline, TimelineItem } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import moment from "moment";
import React from "react";

interface Action {
	item: Enumerated;
	seqNr: number;
	timestamp: string;
	user: Enumerated;
	changes: any;
	oldCaseStage: Enumerated;
	newCaseStage: Enumerated;
}

@observer
export default class HomeCardRecentActions extends React.Component {

	@observable activities: Action[] = [];

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
				heading={<strong>{"Letzte Aktionen"}</strong>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_small"
				footer={<></>}
			>
				{
					!this.activities.length &&
					<p className="slds-m-horizontal_medium">Keine Aktivität.</p>
				}
				{
					!!this.activities.length &&
					<Timeline>
						{
							this.activities.map((a: Action, index: number) => (
								<ActivityCard
									key={"a-" + index}
									activity={a}
									onClick={() => this.onClick(a)}
								/>
							))
						}
					</Timeline>
				}
			</Card >
		);
	}

	private async loadActivityList() {
		const rsp = await API.get(Config.getRestUrl("home", "recentActions/" + session.sessionInfo?.account?.id))
		this.activities = rsp.data;
	}

	private onClick = (a: Action) => {
		const type = a.item.itemType?.id.substring(4);
		window.location.href = "/" + type + "/" + a.item.id;
	}

}

export interface ActivityProps {
	activity: Action;
	onClick?: () => void;
}

@observer
class ActivityCard extends React.Component<ActivityProps> {

	@observable item?: Aggregate;
	rtf = new Intl.RelativeTimeFormat('de', { style: 'narrow' });

	constructor(props: ActivityProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { activity, onClick } = this.props;
		const itemType = activity.item.itemType?.id.substring(4)!;
		const type = EntityTypes[itemType];
		const timestamp = moment(activity.timestamp).fromNow();
		return (
			<TimelineItem
				type={"item"}
				name={activity.item.name}
				icon={<Icon category={type.iconCategory} name={type.iconName} className="slds-timeline__icon" />}
				date={timestamp}
				body={<span>{this.getUserName(activity)} ⋅ {this.getActivityName(activity)}</span>}
				isExpandable={true}
				detail={<></>}
				onClick={(event: any) => { event.preventDefault(); onClick?.(); }}
			/>
		);
	}

	private getUserName(activity: Action) {
		return activity.user.id == session.sessionInfo?.user.id ? "Du" : activity.user.name;
	}

	private getActivityName(activity: Action) {
		if (activity.item.itemType?.id.startsWith("obj")) {
			if (activity.seqNr === 0) {
				return "Eröffnung";
			} else {
				return "Modifikation";
			}
		} else {
			if (activity.seqNr === 0) {
				return "Eröffnung";
			} else if (activity.newCaseStage?.id === activity.oldCaseStage?.id) {
				return "Modifikation";
			} else {
				return "Statusänderung";
			}
		}
	}

}
