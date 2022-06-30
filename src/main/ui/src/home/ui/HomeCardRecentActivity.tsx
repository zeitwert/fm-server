
import { Icon } from "@salesforce/design-system-react";
import { Aggregate, EntityType, EntityTypes, Task, TaskStoreModel } from "@zeitwert/ui-model";
import { TimelineItem } from "@zeitwert/ui-slds/timeline/Timeline";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import moment from "moment";
import React from "react";

export interface Activity {
	objTypeId: string;
	objId: number;
	objCaption: string;
	seqNr: number;
	timestamp: string;
	user: string;
	changes: string;
}

export interface HomeCardRecentActivityProps {
	activity: Activity;
	isExpanded: boolean;
	onClick?: () => void;
}

@observer
export default class HomeCardRecentActivity extends React.Component<HomeCardRecentActivityProps> {

	@observable item?: Aggregate;
	rtf = new Intl.RelativeTimeFormat('de', { style: 'narrow' });

	constructor(props: HomeCardRecentActivityProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { activity, isExpanded, onClick } = this.props;
		const type = EntityTypes[activity.objTypeId.substring(4)];
		const timestamp = moment(activity.timestamp).fromNow();
		return (
			<TimelineItem
				icon={<Icon category={type.iconCategory} name={type.iconName} className="slds-timeline__icon" />}
				name={activity.objCaption}
				date={timestamp}
				type={"what"}
				body={<span>{activity.user}<br />{activity.seqNr ? "Modifikation" : "Eröffnung"}</span>}
				detail={<></>}
				onClick={(event: any) => { event.preventDefault(); onClick?.(); }}
				onToggle={() => this.loadItem()}
				isExpanded={isExpanded}
			/>
		);
	}

	renderAdditionalDetail(activity: Activity) {
		if (!this.item) {
			return <></>;
		}
		switch (activity.objTypeId) {
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
		switch (activity.objTypeId) {
			case EntityType.TASK:
				this.item = await TaskStoreModel.create().load(activity.objId.toString());
				break;
			default:
				break;
		}
	}

}
