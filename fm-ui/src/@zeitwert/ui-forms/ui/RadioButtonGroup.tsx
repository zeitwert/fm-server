
import { Enumerated } from "@zeitwert/ui-model";
import { observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps, getFieldId } from "./Field";

export interface RadioButtonGroupProps extends FieldProps {
	options?: Enumerated[];
}

@observer
export class RadioButtonGroup extends React.Component<RadioButtonGroupProps> {

	static contextType = FormContext;

	context!: React.ContextType<typeof FormContext>;

	@observable
	showHelpText: boolean = false;

	render() {
		const accessor = getAccessor(this.props, this.context);
		const { options } = this.props;
		const radioOptions = (accessor ? (accessor.field.options as any).options : options) as Enumerated[];
		const { readOnly, inputProps } = getComponentProps(accessor, this.props);
		const currentOption = radioOptions.find(o => o.id === inputProps.value);
		const fieldId = getFieldId(this.props);
		return (
			<Field {...this.props} key={fieldId + "-" + accessor?.readOnly}>
				{readOnly && currentOption && <span>{currentOption.name}</span>}
				{readOnly && !currentOption && <span>&nbsp;</span>}
				{
					!readOnly &&
					<div className="slds-radio_button-group" id={fieldId}>
						{
							radioOptions.map(option => {
								return (
									<span className="slds-button slds-radio_button" key={fieldId + "-" + option.id}>
										<input
											type="radio"
											id={fieldId + "-" + option.id}
											name={fieldId + "-" + option.id}
											value={option.id}
											checked={inputProps.value === option.id}
											onChange={(e) => { accessor?.setRaw(option.id); }}
										/>
										<label className="slds-radio_button__label" htmlFor={fieldId + "-" + option.id}>
											<span className="slds-radio_faux">{option.name}</span>
										</label>
									</span>
								);
							})
						}
					</div>
				}
			</Field>
		);
	}

}
