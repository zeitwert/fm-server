import { types } from "mobx-state-tree";
import moment from "moment";

const AnyModel = types.custom<any, any>({
	name: "",
	isTargetType() {
		return true;
	},
	getValidationMessage() {
		return "";
	},
	fromSnapshot(value: any) {
		return value;
	},
	toSnapshot(value) {
		return value;
	}
});

const DateModel = types.custom<string | undefined, Date | undefined>({
	name: "",
	isTargetType(value: string | Date | undefined) {
		return value instanceof Date;
	},
	getValidationMessage(snapshot: string | undefined) {
		return "";
	},
	fromSnapshot(value: string | undefined) {
		if (value === undefined) {
			return undefined;
		}
		//const date = moment.utc(value);
		const date = moment(value);
		return date.isValid() ? date.toDate() : undefined;
	},
	toSnapshot(value: Date | undefined) {
		if (value === undefined) {
			return undefined;
		}
		//const date = moment.utc(value);
		const date = moment(value);
		return date.isValid() ? date.format("YYYY-MM-DDTHH:mm:ss") : undefined;
	}
});

const DateWithOffsetModel = types.custom<string | undefined, Date | undefined>({
	name: "",
	isTargetType(value: string | Date | undefined) {
		return value instanceof Date;
	},
	getValidationMessage(snapshot: string | undefined) {
		return "";
	},
	fromSnapshot(value: string | undefined) {
		if (value === undefined) {
			return undefined;
		}
		//const date = moment.utc(value);
		const date = moment(value);
		return date.isValid() ? date.toDate() : undefined;
	},
	toSnapshot(value: Date | undefined) {
		if (value === undefined) {
			return undefined;
		}
		//const date = moment.utc(value);
		const date = moment(value);
		return date.isValid() ? date.format("YYYY-MM-DDTHH:mm:ssZ") : undefined;
	}
});

export const faTypes = {
	any: AnyModel,
	date: DateModel,
	dateWithOffset: DateWithOffsetModel
};
