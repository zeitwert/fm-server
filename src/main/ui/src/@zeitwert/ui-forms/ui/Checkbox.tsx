
import { SLDSCheckbox } from "@salesforce/design-system-react";
import { observer } from "mobx-react";
import { FC, useContext } from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps } from "./Field";

export interface CheckboxProps extends FieldProps {
	checked?: boolean;
}

export const Checkbox: FC<CheckboxProps> = observer((props) => {
	const accessor = getAccessor(props, useContext(FormContext));
	const { readOnly, inputProps } = getComponentProps(accessor, props);
	return (
		<Field {...props} readOnly={false}>
			{
				readOnly &&
				<SLDSCheckbox
					variant="toggle"
					labels={{ toggleDisabled: "", toggleEnabled: "" }}
					disabled={true}
					checked={inputProps.checked}
				/>
			}
			{
				!readOnly &&
				<SLDSCheckbox
					variant="toggle"
					labels={{ toggleDisabled: "", toggleEnabled: "" }}
					disabled={inputProps.disabled}
					onChange={(event: any, checked: boolean) => { console.log("onChange", event, checked); inputProps.onChange(event, checked); }}
					checked={inputProps.checked}
				/>
			}
		</Field>
	);
});
