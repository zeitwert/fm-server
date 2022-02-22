import { Form, FormEventsProps } from "@finadvise/forms";
import { Config, FORM_API, jsonApiFetch } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

const API_BASE_URL = Config.getApiUrl("##", "##").replace("/##/##", "");
const ENUM_BASE_URL = Config.getEnumUrl("##", "##").replace("/##/##", "");

interface FormWrapperProps {
	formId?: string;
	formDefinition?: any;
	payload?: any;
	additionalData?: any;
	displayMode?: "enabled" | "disabled" | "readonly";
	debug?: boolean;
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
			<Form
				config={this.config}
				payload={this.props.payload}
				additionalData={Object.assign({}, this.props.additionalData, {
					apiBaseUrl: API_BASE_URL,
					enumBaseUrl: ENUM_BASE_URL
				})}
				enabled={this.props.displayMode ? this.props.displayMode === "enabled" : true}
				readOnly={this.props.displayMode ? this.props.displayMode === "readonly" : false}
				debug={this.props.debug}
				fetch={jsonApiFetch}
				onEvent={this.props.onEvent}
				onReady={this.props.onReady}
				onValidChange={this.props.onValidChange}
				onBeforeChange={this.props.onBeforeChange}
				onAfterChange={this.props.onAfterChange}
				onFocus={this.props.onFocus}
				onBlur={this.props.onBlur}
				onUploadSelect={this.props.onUploadSelect}
				onLinkClick={this.props.onLinkClick}
				onButtonClick={this.props.onButtonClick}
				onOutcomeClick={this.props.onOutcomeClick}
				onWizardPrev={this.props.onWizardPrev}
				onWizardNext={this.props.onWizardNext}
				onSubformAdd={this.props.onSubformAdd}
				onSubformRemove={this.props.onSubformRemove}
				lang={this.ctx.session.locale}
			/>
		);
	}

}
