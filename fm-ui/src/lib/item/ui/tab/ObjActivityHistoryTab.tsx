
import { Icon } from "@salesforce/design-system-react";
import { DateFormat, Obj, ObjPartTransition, session } from "@zeitwert/ui-model";
import { Col, Grid, Row, Timeline, TimelineItem } from "@zeitwert/ui-slds";
import { makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface ObjActivityHistoryTabProps {
	obj: Obj;
}

@observer
export default class ObjActivityHistoryTab extends React.Component<ObjActivityHistoryTabProps> {

	@observable transitions: ObjPartTransition[] = [];

	constructor(props: ObjActivityHistoryTabProps) {
		super(props);
		makeObservable(this);
		this.transitions = Array.from(toJS(this.props.obj.meta!.transitions));
		this.transitions.sort((a, b) => a.timestamp < b.timestamp ? 1 : -1);
	}

	render() {
		console.log("obj activity history tab render", this.transitions);
		return (
			<div className="slds-m-around_medium">
				<Timeline>
					{
						this.transitions.map((transition, index) => (
							<TimelineItem
								key={index}
								type={this.getItemType(transition)}
								name={this.getActivity(transition)}
								icon={this.getIcon(transition)}
								date={DateFormat.relativeTime(transition.timestamp!)}
								body={this.getUserName(transition)}
								isExpandable={true}
								isExpanded={index === 0}
								detail={
									<Grid isVertical={true}>
										<Row>
											<Col className="slds-size_4-of-12">
												<div className="slds-text-color_weak">
													<strong>Zeitpunkt:</strong>
												</div>
											</Col>
											<Col className="slds-size_8-of-12">
												<div>{DateFormat.long(transition.timestamp)}</div>
											</Col>
										</Row>
										<Row>
											<Col className="slds-size_4-of-12">
												<div className="slds-text-color_weak">
													<strong>Dauer:</strong>
												</div>
											</Col>
											<Col className="slds-size_8-of-12">
												<div>{this.getDuration(index)}</div>
											</Col>
										</Row>
									</Grid>
								}
							/>
						))
					}
					<TimelineItem
						type="stage"
						name=""
						icon={<Icon category="utility" name="routing_offline" size="medium" className="slds-timeline__icon" />}
						date=""
						body=""
						detail=""
						isExpandable={false}
						isExpanded={false}
					/>
				</Timeline>
			</div>
		);
	}

	private getUserName(transition: ObjPartTransition) {
		return transition.user.id == session.sessionInfo?.user.id ? "Du" : transition.user.name;
	}

	private getItemType(transition: ObjPartTransition) {
		if (transition.seqNr === 0) {
			return "stage";
		} else {
			return "event";
		}
	}

	private getIcon(transition: ObjPartTransition) {
		if (transition.seqNr === 0) {
			return <Icon category="standard" name="stage" size="medium" className="slds-timeline__icon" />;
		} else {
			return <Icon category="standard" name="record_update" size="medium" className="slds-timeline__icon" />;
		}
	}

	private getActivity(transition: ObjPartTransition) {
		if (transition.seqNr === 0) {
			return "Eröffnung";
		} else {
			return "Modifikation";
		}
	}

	private getDuration(index: number) {
		return "tbd";
	}

}
