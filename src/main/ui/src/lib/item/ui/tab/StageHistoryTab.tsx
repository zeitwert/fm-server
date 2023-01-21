
import { Icon } from "@salesforce/design-system-react";
import { DateFormat, Doc } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { Timeline, TimelineItem } from "@zeitwert/ui-slds/timeline/Timeline";
import { observer } from "mobx-react";
import React from "react";

interface StageHistoryTabProps {
	doc: Doc;
}

@observer
export default class StageHistoryTab extends React.Component<StageHistoryTabProps> {

	getStartDate(index: number) {
		return this.props.doc.meta!.transitions?.[index ? index - 1 : index]?.timestamp;
	}

	render() {
		const transitions = this.props.doc.meta!.transitions;
		return (
			<div className="slds-m-around_medium">
				{!transitions.length && <div>No case stage history.</div>}
				<Timeline>
					{
						transitions.map((transition, index) => (
							<TimelineItem
								key={index}
								name={transition.newCaseStage!.name}
								type="stage"
								icon={<Icon category="standard" name="stage" size="medium" className="slds-timeline__icon" />}
								date={DateFormat.long(transition.timestamp!)}
								body={transition.user?.name + " ⋅ " + DateFormat.relativeTime(transition.timestamp!)}
								detail={
									<Grid isVertical={false}>
										<Col className="slds-size_5-of-12">
											<div className="slds-text-color_weak">
												<strong>Start Date:</strong>
											</div>
										</Col>
										<Col className="slds-size_7-of-12">
											<div>{DateFormat.long(this.getStartDate(index))}</div>
										</Col>
									</Grid>
								}
								isExpanded={index === 0}
							/>
						))
					}
				</Timeline>
			</div>
		);
	}

}
