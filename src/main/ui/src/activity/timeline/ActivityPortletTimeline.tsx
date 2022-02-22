import ExpandableSection from "@salesforce/design-system-react/components/expandable-section";
import { Activity, DateFormat } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds/common/Grid";
import { Timeline } from "@zeitwert/ui-slds/timeline/Timeline";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import moment from "moment";
import React from "react";
import ActivityPortletTimelineItem from "./ActivityPortletTimelineItem";

interface ActivityPortletTimelineProps extends RouteComponentProps {
	activities: Activity[];
}

@observer
class ActivityPortletTimeline extends React.Component<ActivityPortletTimelineProps> {
	@observable isUpcomingActivitiesOpen = true;
	@observable isOverdueActivitiesOpen = true;
	@observable pastActivityOpenStates = new Map<string, boolean>();

	constructor(props: ActivityPortletTimelineProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { activities } = this.props;
		const upcomingActivities = activities.filter((a) => a.isUpcoming);
		const overdueActivities = activities.filter((a) => a.isOverdue);
		const pastActivities = new Map<string, Activity[]>();
		activities
			.filter((a) => a.isPast)
			.forEach((a) => {
				const key = moment(a.date).format("YYYY-MM");
				let month: Activity[] = [];
				if (pastActivities.has(key)) {
					month = pastActivities.get(key)!;
				} else {
					pastActivities.set(key, month);
				}
				month.push(a);
			});
		return (
			<>
				<ExpandableSection
					id="upcoming-activities"
					/* @ts-ignore */
					title={<span className="slds-text-title_bold">Upcoming</span>}
					isOpen={this.isUpcomingActivitiesOpen}
					onToggleOpen={() => (this.isUpcomingActivitiesOpen = !this.isUpcomingActivitiesOpen)}
				>
					{this.renderTimeline(upcomingActivities, "No upcoming activities.", true)}
				</ExpandableSection>
				<ExpandableSection
					id="overdue-activities"
					/* @ts-ignore */
					title={<span className="slds-text-title_bold">Overdue</span>}
					isOpen={this.isOverdueActivitiesOpen}
					onToggleOpen={() => (this.isOverdueActivitiesOpen = !this.isOverdueActivitiesOpen)}
				>
					{this.renderTimeline(overdueActivities, "No overdue activities.", true)}
				</ExpandableSection>
				{this.renderPastActivities(pastActivities)}
			</>
		);
	}

	private renderTimeline(activities: Activity[], message: string, expandFirst: boolean) {
		if (!activities?.length) {
			return <p className="slds-m-horizontal_medium">{message}</p>;
		}
		return (
			<Timeline>
				{activities.map((activity: Activity, index: number) => (
					<ActivityPortletTimelineItem
						key={index}
						activity={activity}
						onClick={() => this.props.navigate("/" + activity.type.type + "/" + activity.id)}
						isExpanded={expandFirst ? index === 0 : false}
					/>
				))}
			</Timeline>
		);
	}

	private renderPastActivities(activities: Map<string, Activity[]>) {
		const items: JSX.Element[] = [];
		activities.forEach((activities, key) => {
			const date = moment(key, "YYYY-MM");
			items.push(
				<ExpandableSection
					key={key}
					id={"past-activities-" + key}
					className="fa-activity-timeline"
					title={
						(
							<Grid isVertical={false}>
								<Col className="slds-text-title_bold">{date.format("MMMM - YYYY")}</Col>
								<Col className="slds-text-title_bold slds-text-align_right">
									{DateFormat.relativeDate(date.toDate())}
								</Col>
							</Grid>
						) as unknown as string
					}
					isOpen={this.pastActivityOpenStates.has(key) ? this.pastActivityOpenStates.get(key) : false}
					onToggleOpen={() =>
						this.pastActivityOpenStates.has(key)
							? this.pastActivityOpenStates.set(key, !this.pastActivityOpenStates.get(key)!)
							: this.pastActivityOpenStates.set(key, true)
					}
				>
					{this.renderTimeline(activities, "No past activities.", false)}
				</ExpandableSection>
			);
		});
		return items;
	}
}

export default withRouter(ActivityPortletTimeline);
