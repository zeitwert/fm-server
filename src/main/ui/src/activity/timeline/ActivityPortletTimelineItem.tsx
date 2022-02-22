import {
	Activity,
	Aggregate,
	DateFormat,
	EntityType,
	Task,
	TaskStoreModel
} from "@comunas/ui-model";
import { TimelineItem } from "@comunas/ui-slds/timeline/Timeline";
import Icon from "@salesforce/design-system-react/components/icon";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface ActivityPortletTimelineItemProps {
	activity: Activity;
	isExpanded: boolean;
	onClick?: () => void;
}

@observer
export default class ActivityPortletTimelineItem extends React.Component<ActivityPortletTimelineItemProps> {

	@observable item?: Aggregate;

	constructor(props: ActivityPortletTimelineItemProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { activity, isExpanded, onClick } = this.props;
		return (
			<TimelineItem
				name={activity.caption!}
				type={activity.type.type}
				icon={
					<Icon
						category={activity.type.iconCategory}
						name={activity.type.iconName}
						className="slds-timeline__icon"
					/>
				}
				date={DateFormat.short(activity.date!)}
				body={"Assigned to " + activity.assignee.caption}
				detail={
					<>
						<p className="slds-m-bottom_x-small">{activity.timelineDescription}</p>
						<p className="slds-m-bottom_x-small">
							<strong>Description</strong>:{" "}
							{(activity as any)?.description || "No description"}
						</p>
						{this.renderAdditionalDetail(activity)}
					</>
				}
				onClick={(event: any) => { event.preventDefault(); onClick && onClick(); }}
				onToggle={() => this.loadItem()}
				isExpanded={isExpanded}
			/>
		);
	}

	renderAdditionalDetail(activity: Activity) {
		if (!this.item) {
			return <></>;
		}
		switch (activity.type.type) {
			case EntityType.TASK:
				const task = this.item as Task;
				return (
					<p className="slds-m-bottom_x-small">
						<strong>Priority:</strong> {task.taskPriority?.name}
					</p>
				);
			default:
				return <></>;
		}
	}

	async loadItem() {
		if (this.item) {
			return;
		}
		const { activity } = this.props;
		switch (activity.type.type) {
			case EntityType.TASK:
				this.item = await TaskStoreModel.create().load(activity.id);
				break;
			default:
				break;
		}
	}
}
