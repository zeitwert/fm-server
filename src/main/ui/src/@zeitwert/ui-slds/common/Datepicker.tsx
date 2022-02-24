
import { default as BaseDatepicker } from "@salesforce/design-system-react/components/date-picker";
import { DATE_FORMAT } from "@zeitwert/ui-model";
import moment from "moment";
import React from "react";

interface DatepickerProps {
	label: string;
	value?: Date;
	isRequired?: boolean;
	isOnlyPast?: boolean;
	isOnlyFuture?: boolean;
	onChange: (date: Date) => void;
}

export default class Datepicker extends React.Component<DatepickerProps> {
	render() {
		const { label, value, isRequired, isOnlyPast, isOnlyFuture, onChange } = this.props;
		return (
			<BaseDatepicker
				labels={{
					label: label
				}}
				value={value}
				formatter={(date: Date) => (date ? moment(date).format(DATE_FORMAT) : "")}
				parser={(dateString: string) => moment(dateString, DATE_FORMAT).toDate()}
				dateDisabled={(data: { date: Date }) => {
					if (isOnlyPast) {
						return moment().isSameOrBefore(moment(data.date));
					} else if (isOnlyFuture) {
						return moment().isSameOrAfter(moment(data.date));
					}
					return true;
				}}
				onChange={(event: any, data: any) => onChange(data.date)}
				isIsoWeekday
				required={isRequired}
			/>
		);
	}
}
