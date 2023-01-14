
import { Card } from "@salesforce/design-system-react";
import { Checkbox, Combobox, FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { TaskModelType, TaskStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import TaskForm from "./forms/TaskFormDef";

export interface TaskCreationFormProps {
	store: TaskStore;
}

@observer
export default class TaskCreationForm extends React.Component<TaskCreationFormProps> {

	formStateOptions: FormStateOptions<TaskModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.store.isInTrx) {
				return true;
			}
			return false;
		},
		isDisabled: (accessor) => {
			const task = this.props.store.task!;
			if (["account"].indexOf(accessor.fieldref) >= 0) {
				console.log("account", accessor.value, !!accessor.value);
				return !!accessor.value;
			}
			return !!accessor.fieldref && !task.account;
		},
	};

	render() {
		return (
			<SldsForm
				formModel={TaskForm}
				formStateOptions={this.formStateOptions}
				item={this.props.store.task!}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Combobox label="Kunde" fieldName="account" />
								</FieldRow>
								<FieldRow>
									<Input label="Titel" fieldName="subject" />
								</FieldRow>
								<FieldRow>
									<TextArea label="Details" fieldName="content" rows={8} />
								</FieldRow>
								<FieldRow>
									<Select label="Priorität" fieldName="priority" size={10} />
									<Checkbox label="Privat?" fieldName="isPrivate" size={2} />
								</FieldRow>
								<FieldRow>
									<Input label="Fällig am" fieldName="dueAt" />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
