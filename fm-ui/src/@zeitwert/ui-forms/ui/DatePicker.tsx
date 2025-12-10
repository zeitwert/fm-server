
import { SLDSDatepicker } from "@salesforce/design-system-react";
import { DateFormat } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import moment from "moment";
import { FC, useContext } from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps } from "./Field";

export enum Format {
	LONGER = "longer",
	LONG = "long",
	SHORT = "short"
}

export interface DatePickerProps extends FieldProps {
	value?: Date;
	onChange?: (value: Date) => void;
	readOnlyFormat?: Format;
	yearRangeMin?: number;
	yearRangeMax?: number;
}

const Labels =
{
	abbreviatedWeekDays: ["So", "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"],
	weekDays: ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],
	months: ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"],
	placeholder: "Datum auswählen",
	today: "Heute"
};

export const DatePicker: FC<DatePickerProps> = observer((props) => {
	const accessor = getAccessor(props, useContext(FormContext));
	const { readOnly, inputProps } = getComponentProps(accessor, props);
	return (
		<Field {...props}>
			{readOnly && !inputProps.value && <span>&nbsp;</span>}
			{readOnly && inputProps.value && <span>{dateFormatter(inputProps.value, props.readOnlyFormat ?? Format.SHORT)}</span>}
			{
				!readOnly &&
				<SLDSDatepicker
					labels={Labels}
					value={inputProps.value}
					triggerClassName="fa-full-width"
					formatter={(date: Date) => (date ? moment(date).format("DD.MM.YYYY") : "")}
					parser={(dateString: string) => moment(dateString, "DD.MM.YYYY").toDate()}
					disabled={inputProps.disabled}
					required={inputProps.required}
					onChange={(event: any, data: any) => {
						const date = moment(data.date).set("hour", 0).set("minute", 0).set("second", 0);
						inputProps.onChange(date);
					}}
					relativeYearFrom={props.yearRangeMin}
					relativeYearTo={props.yearRangeMax}
					isIsoWeekday
				/>
			}
		</Field>
	);
});

const dateFormatter = (date: Date, format: Format) => {
	return DateFormat[format](date, false);
};
