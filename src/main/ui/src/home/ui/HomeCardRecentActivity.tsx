import { Icon } from "@salesforce/design-system-react";
import {
	Aggregate,
	EntityType,
	IconCategory,
	Task,
	TaskStoreModel
} from "@zeitwert/ui-model";
import { TimelineItem } from "@zeitwert/ui-slds/timeline/Timeline";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface User {
	id: string;
	name: string;
}

export interface Building {
	id: string;
	name: string;
	image: string;
}

export interface ActivityType {
	type: string;
	iconCategory: IconCategory;
	iconName: string;
}

export interface Activity {
	id: string;
	building: Building;
	type: ActivityType;
	action: string;
	user: User;
	date: string;
}

export interface HomeCardRecentActivityProps {
	activity: Activity;
	isExpanded: boolean;
	onClick?: () => void;
}

@observer
export default class HomeCardRecentActivity extends React.Component<HomeCardRecentActivityProps> {

	@observable item?: Aggregate;

	constructor(props: HomeCardRecentActivityProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { activity, isExpanded, onClick } = this.props;
		const { building, user } = activity;
		return (
			<TimelineItem
				name={building.name + " (" + building.id + ")"}
				type={activity.type.type}
				icon={
					<Icon
						category={activity.type.iconCategory}
						name={activity.type.iconName}
						className="slds-timeline__icon"
					/>
				}
				date={""}
				body={<span>{activity.action}<br />{user.name + ", " + activity.date}</span>}
				detail={<></>}
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
