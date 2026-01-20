import { DatePicker, Typography } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import dayjs, { Dayjs } from "dayjs";
import "dayjs/locale/de";
import { AfField } from "./AfField";
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
	...fieldProps
}: AfDatePickerProps) {
	const { control } = useFormContext();

	return (
		<AfField name={name} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({
					field: { value, onChange },
				}: {
					field: { value: Date | null | undefined; onChange: (value: Date | null) => void };
				}) => {
					const dateValue = value ? dayjs(value) : null;

					if (readOnly) {
						return (
							<Typography.Text>{dateValue ? dateValue.format(format) : "\u00A0"}</Typography.Text>
						);
					}

					return (
						<DatePicker
							value={dateValue}
							onChange={(date: Dayjs | null) => onChange(date?.toDate() ?? null)}
							format={format}
							disabled={disabled}
							style={{ width: "100%" }}
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
