
import classNames from "classnames";
import { observer } from "mobx-react";
import { FC } from "react";
import { Field, FieldProps, getComponentProps, getFieldId } from "./Field";

export interface CheckboxProps extends FieldProps {
	checked?: boolean;
}

export const Checkbox: FC<CheckboxProps> = observer((props) => {
	const { accessor, align } = props;
	const { readOnly, inputProps } = getComponentProps(accessor, props);
	const fieldId = getFieldId(props);
	return (
		<Field {...props}>
			{
				readOnly &&
				<span>{inputProps.value}</span>
			}
			{
				!readOnly &&
				<input
					id={fieldId}
					type={"checkbox"}
					className={classNames("slds-input", align ? "slds-text-align_" + align : "")}
					{...inputProps}
				/>
			}
		</Field>
	);
});
