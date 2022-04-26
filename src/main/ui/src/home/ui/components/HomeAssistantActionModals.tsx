
import { Button, Modal } from "@salesforce/design-system-react";
import { TaskForm } from "activity/forms/TaskForm";
import React from "react";

interface ModalCallProps {
	close: () => void;
	save: () => void;
	isOpen: boolean;
	convertEnabled?: boolean;
	saveAndConvert?: () => void;
	entity: any;
}

interface ModalCallState {
	name: string;
	subject: string;
	description: string;
}

export class ModalCall extends React.Component<ModalCallProps, ModalCallState> {
	constructor(props: ModalCallProps) {
		super(props);

		this.state = {
			name: "",
			subject: "",
			description: ""
		};
	}

	render() {
		const { isOpen, close, save, saveAndConvert, convertEnabled } = this.props;

		return (
			<Modal
				isOpen={isOpen}
				footer={[
					<Button key="cancel" label="Cancel" onClick={() => close()} />,
					<Button
						key="save"
						label="Save"
						variant="brand"
						onClick={() => {
							save();
							close();
						}}
					/>,
					convertEnabled && (
						<Button
							key="save-and-convert"
							label="Save and Convert"
							variant="brand"
							onClick={() => {
								saveAndConvert && saveAndConvert();
								close();
							}}
						/>
					)
				]}
				onRequestClose={() => close()}
				heading="Log a Call"
			>
				<section className="slds-p-around_large">
					<div className="slds-form-element slds-m-bottom_large">
					</div>
				</section>
			</Modal>
		);
	}
}

interface ModalTaskProps {
	close: () => void;
	save: () => void;
	isOpen: boolean;
	convertEnabled?: boolean;
	saveAndConvert?: () => void;
	entity: any;
}

interface ModalTaskState {
	name: string;
	subject: string;
	description: string;
	dueDate?: Date;
	refDoc: any;
	reminderSet: boolean;
	reminderDate?: Date;
	reminderTime?: string;
}

export class ModalTask extends React.Component<ModalTaskProps, ModalTaskState> {
	constructor(props: ModalTaskProps) {
		super(props);
		this.state = {
			name: "",
			subject: "",
			description: "",
			dueDate: undefined,
			refDoc: undefined,
			reminderSet: false,
			reminderDate: undefined,
			reminderTime: ""
		};
	}

	render() {
		const { isOpen, close, save, saveAndConvert, convertEnabled, entity } = this.props;

		return (
			<Modal
				isOpen={isOpen}
				footer={[
					<Button key="cancel" label="Cancel" onClick={() => close()} />,
					<Button
						key="save"
						label="Save"
						variant="brand"
						onClick={() => {
							save();
							close();
						}}
					/>,
					convertEnabled && (
						<Button
							key="save-and-convert"
							label="Save and Convert"
							variant="brand"
							onClick={() => {
								saveAndConvert && saveAndConvert();
								close();
							}}
						/>
					)
				]}
				onRequestClose={() => close()}
				heading="Create a Task"
			>
				<section className="slds-p-around_large">
					<div className="slds-form-element slds-m-bottom_large">
						<TaskForm
							item={entity}
							account={entity.account}
							onSave={(a: any) => console.log(a) as any}
						/>
					</div>
				</section>
			</Modal>
		);
	}
}
