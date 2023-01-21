
import { Checkbox, FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { DatePicker } from "@zeitwert/ui-forms/ui/DatePicker";
import { Task, TaskModelType } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import TaskForm from "../../../../areas/task/ui/forms/TaskForm";
import { ItemEditorButtons } from "../ItemEditorButtons";

export interface MiniTaskFormProps {
	task: Task;
	isNew: boolean;
	onStart: () => void;
	onCancel: () => Promise<void>;
	onOk: () => Promise<void>;
}

@observer
export default class MiniTaskForm extends React.Component<MiniTaskFormProps> {

	@observable isActive: boolean = false;
	@computed get allowAdd() {
		return !!this.props.task.subject;
	}

	formStateOptions: FormStateOptions<TaskModelType> = {
		// isReadOnly: (accessor) => {
		// 	if (!this.props.isEdit) {
		// 		return true;
		// 	}
		// 	return false;
		// },
		// isDisabled: (accessor) => {
		// 	const task = this.props.store.task!;
		// 	if (["account"].indexOf(accessor.fieldref) >= 0) {
		// 		return !!accessor.value;
		// 	}
		// 	return !!accessor.fieldref && !task.account;
		// },
	};

	constructor(props: MiniTaskFormProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount(): void {
		if (!this.props.isNew) {
			this.isActive = true;
		}
	}

	componentDidUpdate(prevProps: Readonly<MiniTaskFormProps>, prevState: Readonly<{}>, snapshot?: any): void {
		if (!this.props.isNew) {
			this.isActive = true;
		}
	}

	render() {
		return (
			<SldsForm
				formModel={TaskForm}
				formStateOptions={this.formStateOptions}
				item={this.props.task}
			>
				<Grid className="slds-wrap" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<FieldGroup legend={this.isActive ? "Neue Aufgabe" : undefined}>
							<FieldRow>
								<Input
									label={this.isActive ? "Titel" : ""}
									placeholder={this.isActive ? "" : "Neue Aufgabe ..."}
									required={this.isActive}
									fieldName="subject"
									size={this.isActive ? 10 : 12}
									onFocus={this.onStart}
									onKeyDown={(e) => {
										if (e.key === "Escape") {
											this.onCancel();
										}
									}}
								/>
								{
									this.isActive &&
									<Checkbox label="Privat?" fieldName="isPrivate" size={2} />
								}
							</FieldRow>
							{
								this.isActive &&
								<>
									<FieldRow>
										<TextArea label="Details" fieldName="content" rows={6} />
									</FieldRow>
									<FieldRow>
										<Select label="Zugewiesen an" fieldName="assignee" size={5} />
										<DatePicker label="Fällig am" fieldName="dueAt" size={4} yearRangeMin={-1} />
										<Select label="Priorität" fieldName="priority" size={3} />
									</FieldRow>
									<hr style={{ marginBlockStart: "12px", marginBlockEnd: "12px" }} />
									<div style={{ textAlign: "right" }}>
										<ItemEditorButtons
											showEditButtons={true}
											doEdit={true}
											allowStore={this.allowAdd}
											onCancelEditor={this.onCancel}
											onCloseEditor={this.onOk}
										/>
									</div>
								</>
							}
						</FieldGroup>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

	private onStart = () => {
		if (!this.isActive) {
			this.isActive = true;
			this.props.onStart();
		}
	}

	private onCancel = async () => {
		this.isActive = false;
		this.props.onCancel();
	}

	private onOk = async () => {
		await this.props.onOk();
		this.isActive = false;
	}

}
