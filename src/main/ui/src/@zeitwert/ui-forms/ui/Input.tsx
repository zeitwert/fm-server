
import classNames from "classnames";
import { observer } from "mobx-react";
import { FC } from "react";
import { getComponentProps } from "..";
import { Field, FieldProps, getFieldId } from "./Field";

export interface InputProps extends FieldProps {
	id?: string;
	type?: "text" | "password";
	value?: string;
	onFocus?: (e: React.FocusEvent<HTMLInputElement>) => void;
	onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
	onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export const Input: FC<InputProps> = observer((props) => {
	const { id, accessor, type, align } = props;
	const { readOnly, inputProps } = getComponentProps(accessor, props);
	const fieldId = id || getFieldId(props);
	return (
		<Field {...props}>
			{readOnly && inputProps.value && <span>{inputProps.value}</span>}
			{readOnly && !inputProps.value && <span>&nbsp;</span>}
			{
				!readOnly &&
				<input
					id={fieldId}
					type={type || "text"}
					className={classNames("slds-input", align ? "slds-text-align_" + align : "")}
					{...inputProps}
					autoComplete="off"
					onFocus={props.onFocus}
					onKeyDown={props.onKeyDown}
				/>
			}
		</Field>
	);
});
