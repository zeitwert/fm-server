import { Input, Typography } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { AfField } from "./AfField";
import type { AfFieldProps } from "../../types";

interface AfTextAreaProps extends AfFieldProps {
	/** Number of visible text rows */
	rows?: number;
	/** Placeholder text */
	placeholder?: string;
}

/**
 * Multi-line text input field.
 *
 * Replaces `TextArea` from fm-ui. Wraps Ant Design TextArea with React Hook Form.
 *
 * @example
 * <AfTextArea name="description" label="Beschreibung" rows={6} />
 */
export function AfTextArea({
	name,
	rows = 4,
	placeholder,
	readOnly,
	disabled,
	...fieldProps
}: AfTextAreaProps) {
	const { control } = useFormContext();

	return (
		<AfField name={name} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({ field: { value, onChange, onBlur, ref } }) =>
					readOnly ? (
						<Typography.Paragraph style={{ whiteSpace: "pre-wrap", margin: 0 }}>
							{(value as string) || "\u00A0"}
						</Typography.Paragraph>
					) : (
						<Input.TextArea
							ref={ref}
							value={(value as string) ?? ""}
							onChange={(e) => onChange(e.target.value)}
							onBlur={onBlur}
							rows={rows}
							disabled={disabled}
							placeholder={placeholder}
						/>
					)
				}
			/>
		</AfField>
	);
}
