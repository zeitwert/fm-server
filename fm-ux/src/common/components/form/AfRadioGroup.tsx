import { Radio, Typography } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { AfField } from "./AfField";
import { useStyles } from "../../hooks/useStyles";
import { getFlexDirectionStyle } from "../../styles";
import type { AfFieldProps, Enumerated } from "../../types";

interface AfRadioGroupProps extends AfFieldProps {
	/** Available options */
	options: Enumerated[];
	/** Layout direction (default: horizontal) */
	direction?: "horizontal" | "vertical";
}

/**
 * Radio button group for selecting from a small set of options.
 *
 * Replaces `RadioButtonGroup` from fm-ui. Uses button-style radio buttons
 * for a more modern appearance.
 *
 * @example
 * const statusOptions = [
 *   { id: 'active', name: 'Aktiv' },
 *   { id: 'inactive', name: 'Inaktiv' },
 * ];
 *
 * <AfRadioGroup name="status" label="Status" options={statusOptions} />
 */
export function AfRadioGroup({
	name,
	options,
	direction = "horizontal",
	readOnly,
	disabled,
	required,
	...fieldProps
}: AfRadioGroupProps) {
	const { control } = useFormContext();
	const { styles } = useStyles();

	return (
		<AfField name={name} required={required} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({
					field: { value, onChange },
				}: {
					field: { value: string | undefined; onChange: (value: string) => void };
				}) => {
					const currentValue = value as string | undefined;
					const selectedOption = options.find((o) => o.id === currentValue);

					if (readOnly) {
						return (
							<Typography.Text style={styles.readonlyField}>
								{selectedOption?.name || "\u00A0"}
							</Typography.Text>
						);
					}

					return (
						<Radio.Group
							value={currentValue}
							onChange={(e) => onChange(e.target.value)}
							disabled={disabled}
							optionType="button"
							buttonStyle="solid"
							style={getFlexDirectionStyle(direction)}
							className={required ? "af-mandatory" : undefined}
						>
							{options.map((option) => (
								<Radio key={option.id} value={option.id}>
									{option.name}
								</Radio>
							))}
						</Radio.Group>
					);
				}}
			/>
		</AfField>
	);
}
