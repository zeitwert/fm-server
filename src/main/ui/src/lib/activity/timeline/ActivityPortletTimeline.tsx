import { ExpandableSection } from "@salesforce/design-system-react";
import { Activity, DateFormat } from "@zeitwert/ui-model";
import { Col, Grid, Timeline } from "@zeitwert/ui-slds";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import moment from "moment";
import React from "react";
import ActivityPortletTimelineItem from "./ActivityPortletTimelineItem";

interface ActivityPortletTimelineProps extends RouteComponentProps {
	activities: Activity[];
}

@observer
class ActivityPortletTimeline extends React.Component<ActivityPortletTimelineProps> {

	@observable activities: Activity[] = [];
	@observable isUpcomingActivitiesOpen = false;
	@observable isOverdueActivitiesOpen = false;
	@observable isPastActivitiesOpen = true;
	@observable pastActivityOpenStates = new Map<string, boolean>();

	@computed
	get upcomingActivities(): Activity[] {
		return this.activities.filter((a) => a.isUpcoming);
	}

	@computed
	get overdueActivities(): Activity[] {
		return this.activities.filter((a) => a.isOverdue);
	}

	@computed
	get pastActivities(): Activity[] {
		return this.activities.filter((a) => a.isPast);
	}

	constructor(props: ActivityPortletTimelineProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.activities = this.props.activities;
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		this.activities = this.props.activities;
	}

	render() {
		const pastActivities = new Map<string, Activity[]>();
		this.pastActivities
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
					title="Bevorstehend"
					isOpen={this.isUpcomingActivitiesOpen}
					onToggleOpen={() => (this.isUpcomingActivitiesOpen = !this.isUpcomingActivitiesOpen)}
				>
					{this.renderTimeline(this.upcomingActivities, "Keine bevorstehende Aktivitäten.", true)}
				</ExpandableSection>
				<ExpandableSection
					id="overdue-activities"
					/* @ts-ignore */
					title="Überfällig"
					isOpen={this.isOverdueActivitiesOpen}
					onToggleOpen={() => (this.isOverdueActivitiesOpen = !this.isOverdueActivitiesOpen)}
				>
					{this.renderTimeline(this.overdueActivities, "Keine überfälligen Aktivitäten.", true)}
				</ExpandableSection>
				<ExpandableSection
					id="past-activities"
					/* @ts-ignore */
					title="Vergangenheit"
					isOpen={this.isPastActivitiesOpen}
					onToggleOpen={() => (this.isPastActivitiesOpen = !this.isPastActivitiesOpen)}
				>
					{this.renderPastActivities(pastActivities)}
				</ExpandableSection>
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
		if (!activities?.size) {
			return <p className="slds-m-horizontal_medium">Keine vergangene Aktivitäten.</p>;
		}
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
					{this.renderTimeline(activities, "Keine vergangene Aktivitäten.", false)}
				</ExpandableSection>
			);
		});
		return items;
	}
}

export default withRouter(ActivityPortletTimeline);
