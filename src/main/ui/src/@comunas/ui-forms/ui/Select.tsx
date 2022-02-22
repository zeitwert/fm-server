
import { Enumerated } from "@comunas/ui-model";
import { observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Field, FieldProps, getComponentProps, getFieldId } from "./Field";

export interface SelectProps extends FieldProps {
	value?: String | undefined;
	values?: Enumerated[];
	onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
}

@observer
export class Select extends React.Component<SelectProps> {

	@observable
	showHelpText: boolean = false;

	async componentDidMount() {
		await this.props.accessor?.references?.load({});
	}

	render() {
		const accessor = this.props.accessor!;
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
