import { Input, Typography } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { AfField } from "./AfField";
import type { AfFieldProps } from "../../types";
import { useStyles } from "../../hooks/useStyles";

interface AfInputProps extends AfFieldProps {
	/** Input type */
	type?: "text" | "password";
	/** Placeholder text */
	placeholder?: string;
	/** Text alignment */
	align?: "left" | "center" | "right";
}

/**
 * Text input field with read-only mode support.
 *
 * Replaces `Input` from fm-ui. Wraps Ant Design Input with React Hook Form.
 *
 * @example
 * <AfInput name="name" label="Name" required />
 * <AfInput name="buildingNr" label="Nr" size={3} align="right" />
 * <AfInput name="password" label="Passwort" type="password" />
 */
export function AfInput({
	name,
	type = "text",
	placeholder,
	align,
	readOnly,
	disabled,
	...fieldProps
}: AfInputProps) {
	const { control } = useFormContext();
	const { styles } = useStyles();

	return (
		<AfField name={name} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({ field: { value, onChange, onBlur, ref } }) =>
					readOnly ? (
						<Typography.Text style={styles.readonlyFieldAligned(align)}>
							{(value as string) || "\u00A0"}
						</Typography.Text>
					) : (
						<Input
							ref={ref}
							value={(value as string) ?? ""}
							onChange={(e) => onChange(e.target.value)}
							onBlur={onBlur}
							type={type}
							disabled={disabled}
							placeholder={placeholder}
							style={{ textAlign: align }}
							autoComplete="off"
						/>
					)
				}
			/>
		</AfField>
	);
}
