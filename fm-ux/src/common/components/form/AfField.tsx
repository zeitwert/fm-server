import { Form, Tooltip } from "antd";
import { QuestionCircleOutlined } from "@ant-design/icons";
import { useFormContext } from "react-hook-form";
import type { ReactNode } from "react";
import type { AfFieldProps } from "../../types";

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
 * - Grid-based sizing (1-12 columns)
 *
 * This component is used internally by all Af* input components.
 */
export function AfField({
	name,
	label,
	required,
	helpText,
	size = 12,
	children,
}: AfFieldInternalProps) {
	const {
		formState: { errors },
	} = useFormContext();

	// Support nested paths like "currentRating.ratingDate"
	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const error = name ? name.split(".").reduce<any>((obj, key) => obj?.[key], errors) : undefined;
	const errorMessage = error?.message as string | undefined;

	return (
		<div style={{ width: `${(size / 12) * 100}%`, padding: "0 8px", boxSizing: "border-box" }}>
			<Form.Item
				label={
					label && (
						<span>
							{label}
							{helpText && (
								<Tooltip title={helpText}>
									<QuestionCircleOutlined style={{ marginLeft: 4, color: "#999" }} />
								</Tooltip>
							)}
						</span>
					)
				}
				required={required}
				validateStatus={errorMessage ? "error" : undefined}
				help={errorMessage}
				style={{ marginBottom: 16 }}
			>
				{children}
			</Form.Item>
		</div>
	);
}
