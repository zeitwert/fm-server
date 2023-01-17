import { Icon, Tooltip } from "@salesforce/design-system-react";
import { CaseStage, DateFormat, DocPartTransition } from "@zeitwert/ui-model";
import classNames from "classnames";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import moment from "moment";
import React from "react";

export enum StageType {
	complete = "complete",
	current = "current",
	expired = "expired",
	incomplete = "incomplete",
	lost = "lost",
	won = "won"
}

interface CasePathStageProps {
	stage: CaseStage;
	stageType?: StageType;
	isActive?: boolean;
	isLast?: boolean;
	transition?: DocPartTransition;
	onSelect?: (stage: CaseStage) => void;
}

@observer
export class CasePathStage extends React.Component<CasePathStageProps> {

	@observable isPopoverOpen = false;

	@computed get name() {
		const { stage, stageType, isLast } = this.props;
		if (isLast) {
			return stage.name;
		}
		switch (stageType) {
			case StageType.won:
			case StageType.lost:
			case StageType.complete:
				return stage.name;
			case StageType.current:
			case StageType.incomplete:
			default:
				return stage.name;
		}
	}

	@computed get isExpired() {
		const { stage, stageType, transition } = this.props;
		if (!transition) {
			return false;
		}
		const dueDate = moment(transition.modifiedAt!).add(stage.due, "days");
		return stageType === StageType.current && !dueDate.isAfter(moment());
	}

	constructor(props: CasePathStageProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { stage, stageType, isActive, transition, onSelect } = this.props;
		const isIncomplete = stageType === StageType.incomplete;
		const classes = classNames("slds-path__item", `slds-is-${stageType}`, {
			"slds-is-active": isActive,
			"slds-is-complete": stageType === StageType.won || stageType === StageType.lost,
			"slds-is-lost": this.isExpired
		});
		return (
			<>
				<li
					className={classes}
					role="presentation"
					onMouseEnter={!isIncomplete && transition ? () => (this.isPopoverOpen = true) : undefined}
					onMouseLeave={!isIncomplete && transition ? () => (this.isPopoverOpen = false) : undefined}
				>
					<span
						aria-selected={isActive}
						className="slds-path__link"
						role="option"
						tabIndex={stageType === StageType.current ? 0 : -1}
						onClick={() => onSelect && onSelect(stage)}
					>
						{
							!isIncomplete && transition && (
								<div style={{ position: "absolute", bottom: "-0.5rem", left: "50%" }}>
									<Tooltip
										align="bottom"
										content={this.renderTooltipContent(transition)}
										isOpen={this.isPopoverOpen}
										position="overflowBoundaryElement"
									>
										<></>
									</Tooltip>
								</div>
							)
						}
						<span className="slds-path__stage">
							<Icon category="utility" name="check" size="x-small" inverse />
						</span>
						<span className="slds-path__title">{this.name}</span>
					</span>
				</li>
			</>
		);
	}

	renderTooltipContent(transition: DocPartTransition) {
		const { stage, stageType } = this.props;
		if (stageType === StageType.current) {
			// Calculate expire date.
			const dueDate = moment(transition.modifiedAt!).add(stage.due, "days");
			// Check if expired.
			if (dueDate.isAfter(moment())) {
				return "Expires in " + DateFormat.relativeTime(dueDate.toDate());
			}
			return "Expired " + DateFormat.relativeTime(dueDate.toDate());
		}
		return "Completed " + DateFormat.relativeTime(transition.modifiedAt!);
	}

}
