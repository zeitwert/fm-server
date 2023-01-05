
import { Enumerated } from "@zeitwert/ui-model";
import { observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps, getFieldId } from "./Field";

export interface SelectProps extends FieldProps {
	value?: String | undefined;
	values?: Enumerated[];
	onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
}

@observer
export class Select extends React.Component<SelectProps> {

	static contextType = FormContext;

	context!: React.ContextType<typeof FormContext>;

	@observable
	showHelpText: boolean = false;

	async componentDidMount() {
		const accessor = getAccessor(this.props, this.context);
		await accessor?.references?.load({});
	}

	render() {
		const accessor = getAccessor(this.props, this.context);
		const { readOnly, inputProps } = getComponentProps(accessor, this.props);
		const items = accessor ? accessor.references.values({}) : this.props.values || [];
		const currentItem = accessor ? accessor.references.getById(accessor.raw) : this.props.values?.find(item => item.id === this.props.value);
		const fieldId = getFieldId(this.props);
		return (
			<Field {...this.props}>
				{readOnly && currentItem && <span>{currentItem?.name}</span>}
				{readOnly && !currentItem && <span>&nbsp;</span>}
				{
					!readOnly &&
					<div className="slds-select_container">
						<select
							id={fieldId}
							className="slds-select"
							{...inputProps}
						>
							<option value="">Select…</option>
							{
								items &&
								items.map(item => <option key={fieldId + "-option-" + item.id} value={item.id}>{item.name}</option>)
							}
						</select>
					</div>
				}
			</Field>
		);
	}

}
