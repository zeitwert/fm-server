
import { Icon } from "@salesforce/design-system-react";
import { Aggregate, ContactStore, DateFormat, EntityType, EntityTypes } from "@zeitwert/ui-model";
import { TimelineItem } from "@zeitwert/ui-slds/timeline/Timeline";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";

interface LifeEventTimelineItemProps {
	lifeEvent: any;
}

@observer
export default class LifeEventTimelineItem extends React.Component<LifeEventTimelineItemProps> {

	@observable item?: Aggregate;
	@observable contactStore?: ContactStore;

	constructor(props: LifeEventTimelineItemProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const anniversary = this.props.lifeEvent!;
		const type = EntityTypes[EntityType.TASK/*ANNIVERSARY*/];
		return (
			<TimelineItem
				name={anniversary.name! ? anniversary.name! : "Noname"}
				type={anniversary.lifeEventTypeId! ? anniversary.lifeEventTypeId! : "NoID"}
				icon={
					<Icon
						assistiveText={{ label: type.labelSingular }}
						category={type.iconCategory}
						name={type.iconName}
					/>
				}
				date={DateFormat.short(anniversary.startDate!, false)}
				body={
					<div>
						<Link to={"/contact/" + anniversary.objId}></Link>
						<div>{"Contact: " + anniversary.contactName}</div>
					</div>
				}
				detail={
					<>
						<p className="slds-m-bottom_x-small">
							<strong>Description</strong>:{" "}
							{anniversary.description! ? anniversary.description! : "No description"}
						</p>
					</>
				}
			/>
		);
	}
}
