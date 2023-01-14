import { Button } from "@salesforce/design-system-react";
import { CaseStage } from "@zeitwert/ui-model";
import classNames from "classnames";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React, { PropsWithChildren } from "react";

interface CasePathProps {
	className?: string;
	targetStage: CaseStage;
	allowTransition: boolean;
	isOtherStageSelected: boolean;
	readOnly?: boolean;
	onModify: () => void;
}

@observer
export class CasePath extends React.Component<PropsWithChildren<CasePathProps>> {
	@observable isShowingDetails = false;
	@observable isEditingDetailFields = false;
	@observable isModifyButtonHovered = false;

	constructor(props: CasePathProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.isShowingDetails = this.props.isOtherStageSelected;
	}

	componentDidUpdate(prevProps: Readonly<CasePathProps>) {
		if (prevProps.isOtherStageSelected !== this.props.isOtherStageSelected) {
			this.isShowingDetails = this.props.isOtherStageSelected;
		}
	}

	render() {
		const { className, targetStage, allowTransition, isOtherStageSelected, readOnly } = this.props;
		const classes = classNames("slds-path", className);
		const trackClasses = classNames("slds-grid slds-path__track");
		const detailButton = (
			<Button
				className="slds-path__trigger"
				title="Expand Stage Explanation"
				onClick={() => (this.isShowingDetails = !this.isShowingDetails)}
				iconCategory="utility"
				iconName={"chevron" + (this.isShowingDetails ? "down" : "right")}
				iconVariant="border-filled"
				variant="icon"
			/>
		);
		const modifyButtonClasses = classNames(
			"slds-button slds-button_brand slds-path__mark-complete",
			isOtherStageSelected ? "slds-path__mark-current" : null
		);
		const modifyButton = (
			<div className="slds-grid slds-path__action">
				<Button
					label={
						!targetStage
							? ""
							: this.isModifyButtonHovered
								? targetStage.name
								: isOtherStageSelected
									? "Mark as Current"
									: "Mark as Complete"
					}
					iconCategory="utility"
					iconName="check"
					iconPosition="left"
					variant="brand"
					className={modifyButtonClasses}
					onClick={() => {
						this.isShowingDetails = false;
						this.props.onModify();
					}}
					onMouseEnter={() => (this.isModifyButtonHovered = true)}
					onMouseLeave={() => (this.isModifyButtonHovered = false)}
					disabled={!allowTransition}
				/>
			</div>
		);
		return (
			<>
				<div className={classes}>
					<div className={trackClasses}>
						<div className="slds-grid slds-path__scroller-container">
							{detailButton}
							<div className="slds-path__scroller" role="application">
								<div className="slds-path__scroller_inner">
									<ul className="slds-path__nav" role="listbox" aria-orientation="horizontal">
										{this.props.children}
									</ul>
								</div>
							</div>
						</div>
						{!readOnly ? modifyButton : null}
					</div>
				</div>
				{this.isShowingDetails && this.renderDetails()}
			</>
		);
	}

	renderDetails() {
		const { targetStage, readOnly } = this.props;
		return (
			<div className="slds-path__content" id="path-coaching-2">
				<div className="slds-path__coach slds-grid">
					<div className="slds-path__keys">
						<div className="slds-grid slds-grid_align-spread slds-path__coach-title">
							<h2>Key Fields This Stage</h2>
							{!readOnly && (
								<button
									className="slds-button slds-path__coach-edit slds-text-body_small"
									onClick={() => (this.isEditingDetailFields = true)}
								>
									Edit
								</button>
							)}
						</div>
					</div>
					<div className="slds-path__guidance">
						<h2 className="slds-path__coach-title">Guidance for Success</h2>
						<div
							className="slds-text-longform slds-path__guidance-content"
							dangerouslySetInnerHTML={{
								__html: targetStage.description
									? targetStage.description
									: "No guidance has been provided."
							}}
						></div>
					</div>
				</div>
			</div>
		);
	}

}
