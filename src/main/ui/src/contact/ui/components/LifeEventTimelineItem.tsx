import { Icon } from "@salesforce/design-system-react";
import { Aggregate, DateFormat, EntityType, EntityTypes, LifeEvent } from "@zeitwert/ui-model";
import { TimelineItem } from "@zeitwert/ui-slds/timeline/Timeline";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface LifeEventTimelineItemProps {
	lifeEvent: any;
	onClick?: (lifeEvent: LifeEvent) => void;
}

@observer
export default class LifeEventTimelineItem extends React.Component<LifeEventTimelineItemProps> {

	@observable item?: Aggregate;

	constructor(props: LifeEventTimelineItemProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const lifeEvent = this.props.lifeEvent!;
		const type = EntityTypes[EntityType.LIFE_EVENT];
		const { onClick } = this.props;
		return (
			<TimelineItem
				name={lifeEvent.name! ? lifeEvent.name! : "Noname"}
				type={lifeEvent.lifeEventTypeId! ? lifeEvent.lifeEventTypeId! : "NoID"}
				icon={
					<Icon
						assistiveText={{ label: type.labelSingular }}
						category={type.iconCategory}
						name={type.iconName}
						style={lifeEvent.isDeterministic ? { backgroundColor: this.iconBackgroundColor() } : undefined}
					/>
				}
				date={DateFormat.short(lifeEvent.startDate!, false)}
				body={""}
				detail={
					<>
						<p className="slds-m-bottom_x-small">
							<strong>Description</strong>:{" "}
							{lifeEvent.description! ? lifeEvent.description! : "No description"}
							<p></p>
							<strong>Anniversary</strong>:{" "}
							{lifeEvent.lifeEventTemplate.name! ? lifeEvent.lifeEventTemplate.name! : "No description"}
						</p>
					</>
				}
				onClick={(event: any) => {
					event.preventDefault();
					onClick && onClick(this.props.lifeEvent);
				}}
			/>
		);
	}

	private iconBackgroundColor = () => {
		//return "#23a074";
		return "#334444";
	};
}
