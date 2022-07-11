import { Card } from "@salesforce/design-system-react";
import { ContactStoreModel, LifeEvent } from "@zeitwert/ui-model";
import { Timeline } from "@zeitwert/ui-slds/timeline/Timeline";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import HomeLifeEventItem from "./HomeLifeEventItem";

interface HomeLifeEventProps { }

@inject("appStore", "session")
@observer
export default class HomeLifeEvent extends React.Component<HomeLifeEventProps> {
	@observable lifeEvents?: LifeEvent[];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: HomeLifeEventProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.lifeEvents = await ContactStoreModel.create({}).loadAllLifeEvents();
	}

	render() {
		if (!this.lifeEvents) {
			return (
				<Card heading="Upcoming Anniversaries" className="fa-height-100" bodyClassName="slds-m-around_none">
					<p className="slds-m-horizontal_small slds-m-bottom_medium">There is no upcoming anniversaries.</p>
				</Card>
			);
		}
		const filteredLifeEvents = this.lifeEvents
			.filter((le) => {
				return le.lifeEventTemplate?.id !== "ign";
			})
			.filter((le) => {
				if (le.startDate) {
					return new Date(le.startDate) < new Date(new Date().setDate(new Date().getDate() + 7));
				}
				return le;
			})
			.sort((a, b) => (a.startDate! < b.startDate! ? -1 : 1));

		return (
			<Card
				heading={"Upcoming Anniversaries (" + filteredLifeEvents.length + ")"}
				className="fa-height-100"
				bodyClassName="slds-m-around_none"
			>
				{filteredLifeEvents.length <= 0 && (
					<p className="slds-m-horizontal_small slds-m-bottom_medium">There is no upcoming anniversaries.</p>
				)}
				<>
					<Timeline>
						{filteredLifeEvents.map((_le, i) => (
							<HomeLifeEventItem key={i} lifeEvent={_le} />
						))}
					</Timeline>
				</>
			</Card>
		);
	}
}
