
import { FORM_API } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface FormWrapperProps {
	formId?: string;
	formDefinition?: any;
	payload?: any;
	additionalData?: any;
	displayMode?: "enabled" | "disabled" | "readonly";
	debug?: boolean;
}

export interface FormEventsProps {
	// onEvent?: (name: string, config: any, state: anyFormState, api: FormApi) => boolean;
	// // Custom events. These are derived from onEvent. These should be the same as FormEvents.
	onReady?: (api: any) => void;
	onValidChange?: (isValid: boolean, api: any) => void;
	// onBeforeChange?: (path: string, value: any, api: FormApi) => boolean | void;
	onAfterChange?: (path: string, value: any, api: any) => void;
	// onFocus?: (path: string, api: FormApi) => void;
	// onBlur?: (path: string, api: FormApi) => void;
	onUploadSelect?: (path: string, value: any, api: any) => void;
	// onLinkClick?: (path: string, url: string, api: FormApi) => boolean | void;
	onButtonClick?: (path: string, script: string, api: any) => boolean | void;
	// onOutcomeClick?: (path: string, script: string, navigationUrl: string, api: FormApi) => boolean | void;
	// onWizardPrev?: (path: string, api: FormApi) => boolean | void;
	// onWizardNext?: (path: string, api: FormApi) => boolean | void;
	onSubformAdd?: (path: string, value: any, api: any) => void;
	onSubformRemove?: (path: string, value: any, api: any) => void;
}

@inject("session")
@observer
export class FormWrapper extends React.Component<FormWrapperProps & FormEventsProps> {

	@observable.shallow config?: any;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: FormWrapperProps & FormEventsProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		if (this.props.formDefinition) {
			this.config = this.props.formDefinition;
		} else if (this.props.formId) {
			this.config = await FORM_API.getDefinition(this.props.formId);
		}
	}

	async componentDidUpdate() {
		if (this.props.formDefinition) {
			this.config = this.props.formDefinition;
		}
	}

	render() {
		if (!this.config) {
			return <div className="slds-p-around_medium">Loading...</div>;
		}
		return (
			<div>TODO</div>
		);
	}

}
