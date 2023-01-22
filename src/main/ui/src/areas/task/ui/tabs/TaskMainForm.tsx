
import { Card } from "@salesforce/design-system-react";
import { Checkbox, FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { DatePicker } from "@zeitwert/ui-forms/ui/DatePicker";
import { Task, TaskModelType } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import TaskForm from "../forms/TaskForm";

export interface TaskMainFormProps {
	task: Task;
	doEdit?: boolean;
}

@observer
export default class TaskMainForm extends React.Component<TaskMainFormProps> {

	formStateOptions: FormStateOptions<TaskModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.doEdit) {
				return true;
			}
			return false;
		},
		// isDisabled: (accessor) => {
		// 	const task = this.props.store.task!;
		// 	if (["account"].indexOf(accessor.fieldref) >= 0) {
		// 		return !session.isKernelTenant || !!accessor.value;
		// 	}
		// 	return !!accessor.fieldref && !task.account;
		// },
	};

	render() {
		return (
			<SldsForm
				formModel={TaskForm}
				formStateOptions={this.formStateOptions}
				item={this.props.task}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Titel" fieldName="subject" size={10} />
									<Checkbox label="Privat?" fieldName="isPrivate" size={2} />
								</FieldRow>
								<FieldRow>
									<TextArea label="Details" fieldName="content" rows={8} />
								</FieldRow>
								<FieldRow>
									<Select label="Zugewiesen an" fieldName="assignee" size={4} />
									<DatePicker label="Fällig am" fieldName="dueAt" size={4} yearRangeMin={-1} />
									<Select label="Priorität" fieldName="priority" size={4} />
								</FieldRow>
								<FieldRow>
									<Select
										label="Referenz"
										value={this.props.task.relatedTo}
										required={true}
										disabled={true}
										size={12}
									/>
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
