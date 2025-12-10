
import moment from "moment";
import "moment/locale/de-ch";
import { Locale } from "../i18n";

moment.locale(Locale.de_ch);
const locale = moment.locale();

const DATETIME_LONGER_OPTIONS = {
	hour12: false,
	weekday: "long",
	year: "numeric",
	month: "long",
	day: "numeric",
	hour: "2-digit",
	minute: "2-digit"
} as Intl.DateTimeFormatOptions;

const DATETIME_LONG_OPTIONS = {
	hour12: false,
	weekday: "short",
	year: "numeric",
	month: "short",
	day: "numeric",
	hour: "2-digit",
	minute: "2-digit"
} as Intl.DateTimeFormatOptions;

const DATETIME_SHORT_OPTIONS = {
	hour12: false,
	year: "numeric",
	month: "short",
	day: "numeric",
	hour: "2-digit",
	minute: "2-digit"
} as Intl.DateTimeFormatOptions;

const DATETIME_COMPACT_OPTIONS = {
	hour12: false,
	year: "numeric",
	month: "numeric",
	day: "numeric",
	hour: "2-digit",
	minute: "2-digit"
} as Intl.DateTimeFormatOptions;

const DATE_LONGER_OPTIONS = {
	hour12: false,
	weekday: "long",
	year: "numeric",
	month: "long",
	day: "numeric"
} as Intl.DateTimeFormatOptions;

const DATE_LONG_OPTIONS = {
	hour12: false,
	weekday: "short",
	year: "numeric",
	month: "short",
	day: "numeric"
} as Intl.DateTimeFormatOptions;

const DATE_SHORT_OPTIONS = {
	hour12: false,
	year: "numeric",
	month: "long",
	day: "numeric"
} as Intl.DateTimeFormatOptions;

const DATE_COMPACT_OPTIONS = {
	hour12: false,
	year: "numeric",
	month: "numeric",
	day: "numeric"
} as Intl.DateTimeFormatOptions;

const TIME_OPTIONS = {
	hour12: false,
	hour: "2-digit",
	minute: "2-digit"
} as Intl.DateTimeFormatOptions;

// Default date format.
export const DATE_FORMAT = "DD.MM.YYYY";

export const DateFormat = {
	longer: (date: Date, withTime = true) => {
		if (date) {
			return new Date(date).toLocaleDateString(locale, withTime ? DATETIME_LONGER_OPTIONS : DATE_LONGER_OPTIONS);
		}
		return "";
	},
	long: (date: Date, withTime = true) => {
		if (date) {
			return new Date(date).toLocaleDateString(locale, withTime ? DATETIME_LONG_OPTIONS : DATE_LONG_OPTIONS);
		}
		return "";
	},
	short: (date: Date | null | undefined, withTime = true) => {
		if (date) {
			return new Date(date).toLocaleDateString(locale, withTime ? DATETIME_SHORT_OPTIONS : DATE_SHORT_OPTIONS);
		}
		return "";
	},
	compact: (date: Date | null | undefined, withTime = true) => {
		if (date) {
			return new Date(date).toLocaleDateString(locale, withTime ? DATETIME_COMPACT_OPTIONS : DATE_COMPACT_OPTIONS);
		}
		return "";
	},
	time: (date: Date | null | undefined) => {
		if (date) {
			return new Date(date).toLocaleTimeString(locale, TIME_OPTIONS);
		}
		return "";
	},
	// TODO: These below methods could be changed with https://momentjs.com/docs/#/displaying/fromnow/
	relative: (date: Date, dflt = "", relativeSuffix = "") => {
		const mDate = moment(date);
		let diff = "";
		if (mDate.isSame(moment(), "day")) {
			diff = "today";
		} else if (mDate.isSame(moment().subtract(1, "day"), "day")) {
			diff = "yesterday";
		}
		if (diff) {
			return diff + (relativeSuffix ? " " + relativeSuffix : "");
		}
		return dflt;
	},
	relativeDate: (date: Date) => {
		const mDate = moment(date);
		const d = moment.duration(moment.utc().diff(mDate));
		if (d.asMonths() > 0) {
			if (d.asMonths() <= 1) {
				return "This month";
			} else if (d.asMonths() <= 2) {
				return "Last month";
			} else {
				return Math.trunc(d.asMonths()) + " months ago";
			}
		}
		return "";
	},
	relativeTime: (date: Date) => {
		return moment(date).fromNow();
		/*
				const current = new Date();
				const mDate = moment(date);
				const d = moment.duration(moment(current).diff(mDate));
				if (mDate.isSame(moment(), "day")) {
					if (d.asSeconds() > 0) {
						// Past dates.
						if (d.asHours() >= 1) {
							const hours = Math.trunc(d.asHours());
							return hours + " " + (hours === 1 ? "hour" : "hours") + " ago";
						} else if (d.asMinutes() >= 1) {
							const minutes = Math.trunc(d.asMinutes());
							return minutes + " " + (minutes === 1 ? "minute" : "minutes") + " ago";
						} else if (d.asSeconds() > 20) {
							return Math.trunc(d.asSeconds()) + " seconds ago";
						}
					} else {
						// Future dates.
						if (Math.abs(d.asHours()) >= 1) {
							const hours = Math.abs(Math.trunc(d.asHours()));
							return hours + " " + (hours === 1 ? "hour" : "hours");
						} else if (Math.abs(d.asMinutes()) >= 1) {
							const minutes = Math.abs(Math.trunc(d.asMinutes()));
							return minutes + " " + (minutes === 1 ? "minute" : "minutes");
						} else if (Math.abs(d.asSeconds()) > 20) {
							return Math.abs(Math.trunc(d.asSeconds())) + " seconds";
						}
					}
					return "just now";
				} else {
					const days = Math.abs(Math.round(d.asDays()));
					if (d.asSeconds() > 0) {
						return days + " " + (days === 1 ? "day" : "days") + " ago";
					} else {
						return days + " " + (days === 1 ? "day" : "days");
					}
				}
		*/
	},
	yearsDiff(dt1: Date, dt2: Date) {
		let diff = (dt2.getTime() - dt1.getTime()) / 1000;
		diff /= 60 * 60 * 24;
		return Math.abs(Math.round(diff / 365.25));
	}
};
