
import { observer } from "mobx-react";
import { FC, useContext } from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps, getFieldId } from "./Field";

export interface TextAreaProps extends FieldProps {
	value?: string;
	rows?: number;
}

export const TextArea: FC<TextAreaProps> = observer((props) => {
	const accessor = getAccessor(props, useContext(FormContext));
	const { rows } = props;
	const { readOnly, inputProps } = getComponentProps(accessor, props);
	const fieldId = getFieldId(props);
	return (
		<Field {...props} isMultiline >
			{
				readOnly &&
				<p style={{ whiteSpace: "pre-line" }} >{inputProps.value ? inputProps.value : <>&nbsp;</>}</p>
			}
			{
				!readOnly &&
				<textarea
					id={fieldId}
					className="slds-textarea"
					{...inputProps}
					rows={rows}
				/>
			}
		</Field >
	);
});
