
import { Icon } from "@salesforce/design-system-react";
import { DateFormat, Doc, DocPartTransition, session } from "@zeitwert/ui-model";
import { Col, Grid, Row } from "@zeitwert/ui-slds";
import { Timeline, TimelineItem } from "@zeitwert/ui-slds/timeline/Timeline";
import { makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface DocStageHistoryTabProps {
	doc: Doc;
}

@observer
export default class DocStageHistoryTab extends React.Component<DocStageHistoryTabProps> {

	@observable transitions: DocPartTransition[] = [];

	constructor(props: DocStageHistoryTabProps) {
		super(props);
		makeObservable(this);
		this.transitions = Array.from(toJS(this.props.doc.meta!.transitions));
		this.transitions.sort((a, b) => a.timestamp < b.timestamp ? 1 : -1);
	}

	render() {
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
								isExpandable={true}
								isExpanded={index === 0}
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

	private getUserName(transition: DocPartTransition) {
		const userName = transition.user.id == session.sessionInfo?.user.id ? "Du" : transition.user.name;
		return transition.newCaseStage.name + " ⋅ " + userName;
	}

	private getItemType(transition: DocPartTransition) {
		if (transition.seqNr === 0) {
			return "stage";
		} else if (transition.newCaseStage?.id === transition.oldCaseStage?.id) {
			return "event";
		} else {
			return "stage";
		}
	}

	private getIcon(transition: DocPartTransition) {
		if (transition.seqNr === 0) {
			return <Icon category="standard" name="stage" size="medium" className="slds-timeline__icon" />;
		} else if (transition.newCaseStage?.id === transition.oldCaseStage?.id) {
			return <Icon category="standard" name="record_update" size="medium" className="slds-timeline__icon" />;
		} else {
			return <Icon category="standard" name="stage" size="medium" className="slds-timeline__icon" />;
		}
	}

	private getActivity(transition: DocPartTransition) {
		if (transition.seqNr === 0) {
			return "Eröffnung";
		} else if (transition.newCaseStage?.id === transition.oldCaseStage?.id) {
			return "Modifikation";
		} else {
			return "Transition";
		}
	}

	private getDuration(index: number) {
		return "tbd";
	}

}
