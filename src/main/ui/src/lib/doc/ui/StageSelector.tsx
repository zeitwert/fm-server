import { Button, Combobox, Modal } from "@salesforce/design-system-react";
import { API, CaseStage, Config } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { makeObservable, observable, transaction } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface StageSelectorProps {
	heading: string;
	abstractTargetStage: CaseStage;
	onStageSelection: (stage: CaseStage) => void;
	onCancel: () => void;
}

@inject("logger")
@observer
export class StageSelector extends React.Component<StageSelectorProps> {
	@observable caseStages: CaseStage[] = [];
	@observable caseStageOptions: any[] = [];
	@observable selectedCaseStage: any;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: StageSelectorProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadCaseStages(this.props.abstractTargetStage);
	}

	render() {
		const { heading, onCancel } = this.props;
		return (
			<Modal
				footer={[
					<Button key="cancel" label="Cancel" onClick={onCancel} />,
					<Button
						key="save"
						label="Save"
						variant="brand"
						onClick={this.onSave}
						disabled={!this.selectedCaseStage}
					/>
				]}
				onRequestClose={onCancel}
				heading={heading}
				size="small"
				isOpen
			>
				<section className="slds-p-around_large">
					{this.caseStageOptions.length > 0 && (
						<div className="slds-form-element slds-m-bottom_large">
							<Combobox
								labels={{
									label: "Stage",
									placeholderReadOnly: "Select Stage"
								}}
								options={this.caseStageOptions}
								events={{
									onSelect: (event: any, data: any) => {
										this.selectedCaseStage = data.selection?.[0];
									}
								}}
								menuPosition="relative"
								selection={[this.selectedCaseStage]}
								variant="readonly"
								required
							/>
						</div>
					)}
					<div style={{ minHeight: "10rem" }} />
				</section>
			</Modal>
		);
	}

	private async loadCaseStages(abstractStage: CaseStage) {
		try {
			const response = await API.get(Config.getEnumUrl("doc", "codeCaseStage") + "/" + abstractStage.caseDefId);
			transaction(() => {
				this.caseStages = response.data.filter((s: CaseStage) => s.abstractCaseStageId === abstractStage.id);
				this.caseStageOptions = this.caseStages.map((s) => ({
					id: s.id,
					label: s.name
				}));
			});
		} catch (error: any) {
			this.ctx.logger.error("Failed to load case stages");
			return Promise.reject(error);
		}
	}

	private onSave = () => {
		this.props.onStageSelection(this.caseStages.find((s) => s.id === this.selectedCaseStage?.id)!);
	};
}
