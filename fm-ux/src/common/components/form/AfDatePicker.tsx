import { DatePicker, Typography } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import dayjs, { Dayjs } from "dayjs";
import "dayjs/locale/de";
import { AfField } from "./AfField";
import { useStyles } from "../../hooks/useStyles";
import type { AfFieldProps } from "../../types";

// Set German locale for date picker
dayjs.locale("de");

interface AfDatePickerProps extends AfFieldProps {
	/** Date format string (default: "DD.MM.YYYY") */
	format?: string;
	/** Minimum selectable date */
	minDate?: Date;
	/** Maximum selectable date */
	maxDate?: Date;
}

/**
 * Date picker with German localization.
 *
 * Replaces `DatePicker` from fm-ui. Uses dayjs for date handling
 * and displays dates in DD.MM.YYYY format by default.
 *
 * @example
 * <AfDatePicker name="ratingDate" label="Bewertungsdatum" required />
 * <AfDatePicker
 *   name="birthDate"
 *   label="Geburtsdatum"
 *   maxDate={new Date()}
 * />
 */
export function AfDatePicker({
	name,
	format = "DD.MM.YYYY",
	minDate,
	maxDate,
	readOnly,
	disabled,
	required,
	...fieldProps
}: AfDatePickerProps) {
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
					field: { value: string | null | undefined; onChange: (value: string | null) => void };
				}) => {
					const dateValue = value ? dayjs(value) : null;

					if (readOnly) {
						return (
							<Typography.Text style={styles.readonlyField}>
								{dateValue ? dateValue.format(format) : "\u00A0"}
							</Typography.Text>
						);
					}

					return (
						<DatePicker
							value={dateValue}
							onChange={(date: Dayjs | null) => onChange(date?.format("YYYY-MM-DD") ?? null)}
							format={format}
							disabled={disabled}
							className={`af-full-width${required ? " af-mandatory" : ""}`}
							minDate={minDate ? dayjs(minDate) : undefined}
							maxDate={maxDate ? dayjs(maxDate) : undefined}
							placeholder="Datum auswählen"
						/>
					);
				}}
			/>
		</AfField>
	);
}
