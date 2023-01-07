
import { Input, InputIcon, MenuDropdown } from "@salesforce/design-system-react";
import { MENU_DROPDOWN_TRIGGER } from "@salesforce/design-system-react/utilities/constants";
import { session } from "@zeitwert/ui-model";
import { inject } from "mobx-react";
import moment from "moment";
import React from "react";

interface TimepickerProps {
	label: string;
	stepInMinutes: number;
	time?: Date;
	minTime?: Date;
	maxTime?: Date;
	onChange?: (time: Date) => void;
}

const TimepickerTrigger = (props: any) => {
	return (
		<div onBlur={props.onBlur} onFocus={props.onFocus} onMouseDown={props.onMouseDown}>
			<Input iconRight={<InputIcon category="utility" name="clock" />} {...props} inputRef={props.triggerRef}>
				{props.menu}
			</Input>
		</div>
	);
};
TimepickerTrigger.displayName = MENU_DROPDOWN_TRIGGER;

/**
 * Wrapper for Timepicker component.
 */
@inject("session")
export default class Timepicker extends React.Component<TimepickerProps> {

	render() {
		const { label, time, onChange } = this.props;
		return (
			<MenuDropdown
				checkmark
				disabled={false}
				inheritTargetWidth
				label={label}
				// inline style override
				menuStyle={{
					maxHeight: "20em",
					overflowX: "hidden",
					minWidth: "100%"
				}}
				onSelect={(val: any) => {
					if (val && val.value) {
						onChange && onChange(val.value);
					}
				}}
				value={time}
				menuPosition="absolute"
				options={this.generateOptions()}
			>
				<TimepickerTrigger type="text" value={time ? this.formatter(time) : ""} />
			</MenuDropdown>
		);
	}

	formatter(date: Date) {
		if (date) {
			return date.toLocaleTimeString(session.locale, {
				hour: "2-digit",
				minute: "2-digit"
			});
		}
		return null;
	}

	parser(timeStr: string) {
		const date = new Date();
		const dateStr = date.toLocaleString(session.locale, {
			year: "numeric",
			month: "numeric",
			day: "numeric"
		});
		return new Date(`${dateStr} ${timeStr}`);
	}

	generateOptions() {
		const { minTime, maxTime, stepInMinutes } = this.props;
		const minDate = minTime !== undefined ? moment(minTime) : moment().startOf("day");
		const maxDate = maxTime !== undefined ? moment(maxTime) : moment().endOf("day");
		const options = [];

		const curDate = new Date(minDate.toDate());

		while (moment(curDate).isSameOrBefore(moment(maxDate))) {
			const formatted = this.formatter(curDate);

			options.push({
				label: formatted,
				value: moment.utc(curDate).toDate()
			});

			curDate.setMinutes(curDate.getMinutes() + stepInMinutes);
		}

		return options;
	}
}
