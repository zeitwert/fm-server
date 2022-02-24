import Card from "@salesforce/design-system-react/components/card";
import { CaseStage, CaseStageType, DocStore } from "@zeitwert/ui-model";
import { CasePath } from "@zeitwert/ui-slds/bpm/CasePath";
import { CasePathStage, StageType } from "@zeitwert/ui-slds/bpm/CasePathStage";
import { AppCtx } from "App";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface ItemPathProps {
	store: DocStore;
	stageList: CaseStage[];
	currentStage: CaseStage;
	readOnly?: boolean;
	handleStageTransition: (stage: CaseStage) => void;
	onTransitionToStage: (stage: CaseStage) => Promise<any>;
}

@inject("logger")
@observer
export default class ItemPath extends React.Component<ItemPathProps> {
	@observable selectedStage?: CaseStage;

	@computed get stageList() {
		return this.props.stageList.filter((stage) => {
			if (stage.caseStageTypeId === CaseStageType.abstract) {
				// abstract stage
				return stage.id !== this.props.currentStage.abstractCaseStageId;
			} else if (stage.abstractCaseStageId) {
				// specific stage to an abstract one
				return stage.id === this.props.currentStage.id;
			} else {
				return true;
			}
		});
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ItemPathProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { store, currentStage, readOnly } = this.props;
		const currentIndex = this.stageList.findIndex((stage) => stage.id === currentStage.id)!;
		const nextIndex = currentIndex === this.stageList.length - 1 ? undefined : currentIndex + 1;
		let targetStage: CaseStage | undefined = undefined;
		if (this.isOtherThanCurrentStageSelected()) {
			targetStage = this.selectedStage;
		} else if (nextIndex) {
			targetStage = this.stageList[nextIndex];
		}
		let stageType = StageType.complete; // complete -> current -> incomplete
		return (
			<>
				<div className="fa-item-path">
					<Card heading="" hasNoHeader bodyClassName="slds-p-horizontal_medium">
						<CasePath
							targetStage={this.selectedStage ? this.selectedStage : currentStage}
							allowTransition={!!targetStage}
							isOtherStageSelected={this.isOtherThanCurrentStageSelected()}
							readOnly={readOnly}
							onModify={() => this.onTransitionToStage(targetStage!)}
						>
							{this.stageList.map((stage, index) => {
								const id = stage.id;
								const isCurrent = id === currentStage.id;
								const isActive = id === this.getActiveStage()?.id;
								const isLast = index === this.stageList.length - 1;
								const transition = isCurrent
									? store.findLatestTransitionTo(stage)
									: store.findLatestTransitionFrom(stage);
								if (isLast) {
									stageType = isCurrent ? this.terminalStageType(stage) : StageType.incomplete;
								} else {
									stageType = isCurrent
										? StageType.current
										: stageType === StageType.complete
											? StageType.complete
											: StageType.incomplete;
								}
								return (
									<CasePathStage
										key={"caseStage:" + index + "-" + store.stageTransitions.length}
										stage={stage}
										stageType={stageType}
										isActive={isActive}
										isLast={isLast}
										transition={transition}
										onSelect={this.onStageClick}
									/>
								);
							})}
						</CasePath>
					</Card>
				</div>
				<div className="fa-item-path-placeholder"></div>
			</>
		);
	}

	private getActiveStage(): CaseStage | undefined {
		return this.selectedStage || this.props.currentStage;
	}

	private isOtherThanCurrentStageSelected() {
		return !!this.selectedStage && this.selectedStage?.id !== this.props.currentStage.id;
	}

	private terminalStageType(stage: CaseStage) {
		return stage.id.endsWith("_lost") ? StageType.lost : StageType.won;
	}

	private onStageClick = (stage: CaseStage) => {
		if (this.props.readOnly) {
			return;
		}
		if (stage.id !== this.selectedStage?.id) {
			this.selectedStage = stage;
		} else {
			this.selectedStage = undefined;
		}
	};

	private onTransitionToStage = async (stage: CaseStage) => {
		try {
			this.selectedStage = undefined;
			if (stage.isAbstract || !!stage.action) {
				this.props.handleStageTransition(stage);
			} else {
				await this.props.onTransitionToStage(stage);
			}
		} catch (error: any) {
			this.ctx.logger.error("Failed to transition to stage");
		}
	};
}
