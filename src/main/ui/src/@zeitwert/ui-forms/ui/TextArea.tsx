
import { observer } from "mobx-react";
import { FC } from "react";
import { Field, FieldProps, getComponentProps, getFieldId } from "./Field";

export interface TextAreaProps extends FieldProps {
	value?: string;
	rows?: number;
}

export const TextArea: FC<TextAreaProps> = observer((props) => {
	const { accessor, rows } = props;
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
