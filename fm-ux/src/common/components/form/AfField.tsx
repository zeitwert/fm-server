import { Form, Tooltip } from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";
import { useFormContext } from "react-hook-form";
import type { ReactNode } from "react";
import type { AfFieldProps } from "../../types";
import { useStyles } from "../../hooks/useStyles";
import { getFieldContainerStyle } from "../../styles";

interface AfFieldInternalProps extends AfFieldProps {
	children: ReactNode;
}

/**
 * Internal base wrapper component that provides consistent field layout.
 *
 * Handles:
 * - Label display with optional required asterisk
 * - Help text tooltip
 * - Error message display from React Hook Form
 * - Grid-based sizing (1-24 columns)
 *
 * This component is used internally by all Af* input components.
 */
export function AfField({
	name,
	label,
	required,
	helpText,
	size = 24,
	children,
}: AfFieldInternalProps) {
	const {
		formState: { errors },
	} = useFormContext();
	const { styles } = useStyles();

	// Support nested paths like "currentRating.ratingDate"
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const error = name ? name.split(".").reduce<any>((obj, key) => obj?.[key], errors) : undefined;
	const errorMessage = error?.message as string | undefined;

	return (
		<div style={getFieldContainerStyle(size)}>
			<Form.Item
				label={
					label && (
						<span>
							{label}
							{required && <span className="af-required-asterisk">*</span>}
							{helpText && (
								<Tooltip title={helpText}>
									<QuestionCircleOutlined className="af-help-icon" />
								</Tooltip>
							)}
						</span>
					)
				}
				required={false}
				validateStatus={errorMessage ? "error" : undefined}
				help={errorMessage}
				style={styles.formItemMargin}
			>
				{children}
			</Form.Item>
		</div>
	);
}
