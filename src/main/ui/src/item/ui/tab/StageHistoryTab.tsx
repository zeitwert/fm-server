import { Currency, DateFormat, DocStore } from "@comunas/ui-model";
import { Col, Grid } from "@comunas/ui-slds/common/Grid";
import { Timeline, TimelineItem } from "@comunas/ui-slds/timeline/Timeline";
import Icon from "@salesforce/design-system-react/components/icon";
import { AppCtx } from "App";
import { inject, observer } from "mobx-react";
import React from "react";

const AMOUNT = 270000;
const PROBABILITY = {
	"opportunity.new": 10,
	"opportunity.qualify": 10,
	"opportunity.analyze": 20,
	"opportunity.educate": 50,
	"opportunity.recommend": 70,
	"opportunity.close_won": 100,
	"opportunity.close_lost": 0
};

interface StageHistoryTabProps {
	store: DocStore;
}

@inject("session")
@observer
export default class StageHistoryTab extends React.Component<StageHistoryTabProps> {
	get ctx() {
		return this.props as any as AppCtx;
	}

	getStartDate(index: number) {
		const { store } = this.props;
		if (index === store.stageTransitions.length - 1) {
			return store.item!.meta!.createdAt!;
		}
		return store.stageTransitions[index + 1].modifiedAt!;
	}

	render() {
		const transitions = this.props.store.stageTransitions;
		return (
			<div className="slds-m-around_medium">
				{!transitions.length && <div>No case stage history.</div>}
				<Timeline>
					{transitions.map((transition, index) => (
						<TimelineItem
							key={index}
							name={transition.newCaseStage!.currentName}
							type="stage"
							icon={
								<Icon category="standard" name="stage" size="medium" className="slds-timeline__icon" />
							}
							date={DateFormat.long(transition.modifiedAt!)}
							body={
								transition.user?.caption +
								" ⋅ " +
								DateFormat.relativeTime(new Date(), transition.modifiedAt!)
							}
							detail={
								<Grid isVertical={false}>
									<Col className="slds-size_5-of-12">
										<div className="slds-text-color_weak">
											<strong>Amount:</strong>
										</div>
										<div className="slds-text-color_weak">
											<strong>Probability:</strong>
										</div>
										<div className="slds-text-color_weak">
											<strong>Start Date:</strong>
										</div>
									</Col>
									<Col className="slds-size_7-of-12">
										<div>
											{this.ctx.session.formatter.formatAmount(
												(AMOUNT * this.probability(transition.oldCaseStage!.id)) / 100,
												Currency.CHF
											)}
										</div>
										<div>
											{this.ctx.session.formatter.formatPercent(
												this.probability(transition.oldCaseStage!.id) / 100
											)}
										</div>
										<div>{DateFormat.long(this.getStartDate(index))}</div>
									</Col>
								</Grid>
							}
							isExpanded={index === 0}
						/>
					))}
				</Timeline>
			</div>
		);
	}

	private probability(stage: string) {
		return PROBABILITY[stage] || 0;
	}
}
