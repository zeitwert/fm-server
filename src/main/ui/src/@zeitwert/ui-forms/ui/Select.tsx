
import { Enumerated, isEnumerated, requireThis } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps, getFieldId } from "./Field";

export interface SelectProps extends FieldProps {
	value?: Enumerated | undefined;
	values?: Enumerated[];
	onChange?: (e: Enumerated | undefined) => void;
}

@observer
export class Select extends React.Component<SelectProps> {

	static contextType = FormContext;

	context!: React.ContextType<typeof FormContext>;

	async componentDidMount() {
		const accessor = getAccessor(this.props, this.context);
		requireThis(!!accessor || (!!this.props.values && !!this.props.onChange), "either accessor or values and onChange must be provided");
		await accessor?.references?.load({});
	}

	render() {
		const accessor = getAccessor(this.props, this.context);
		const { readOnly, inputProps } = getComponentProps(accessor, this.props);
		const { value, values } = this.props;
		const items: Enumerated[] = accessor ? accessor.references.values({})! : values || [];
		const currentItem = accessor ? accessor.raw : value;
		const fieldId = getFieldId(this.props);
		requireThis(isEnumerated(value), "value must be an Enumerated (" + value + ")");
		if (readOnly) {
			return (
				<Field {...this.props}>
					{!!currentItem && <span>{currentItem.name}</span>}
					{!currentItem && <span>&nbsp;</span>}
				</Field>
			);
		}
		const required = this.props.required ?? accessor?.required;
		return (
			<Field {...this.props}>
				<div className="slds-select_container">
					<select
						id={fieldId}
						className="slds-select"
						{...inputProps}
						value={currentItem?.id ?? ""}
						onChange={this.onChange}
					>
						{
							(!currentItem?.id || !required) &&
							<option value="">…</option>
						}
						{
							items &&
							items.map(item => <option key={fieldId + "-option-" + item.id} value={item.id}>{item.name}</option>)
						}
					</select>
				</div>
			</Field>
		);
	}

	private onChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
		const accessor = getAccessor(this.props, this.context);
		let newItem: Enumerated | undefined;
		if (accessor) {
			newItem = e.target.value ? accessor.references.getById(e.target.value) : undefined;
		} else {
			newItem = e.target.value ? this.props.values?.find(item => item.id === e.target.value) : undefined;
		}
		if (this.props.onChange) {
			this.props.onChange?.(newItem);
		} else {
			accessor!.setRaw(newItem);
		}
	}

}
