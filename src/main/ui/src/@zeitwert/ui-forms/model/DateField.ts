
import moment from "moment";
import { converters, Field, FieldOptions, StringConverter } from "mstform";

const DefaultDateFormat = "DD.MM.YYYY";

export interface DateConverterOptions {
	format?: string;
}

export function dateConverter(options?: DateConverterOptions) {
	return new StringConverter<Date | undefined>({
		emptyRaw: "",
		emptyValue: undefined,
		convert(raw) {
			return raw ? moment(raw, options?.format || DefaultDateFormat).toDate() : this.emptyValue;
		},
		render(value) {
			return value ? moment(value).format(options?.format || DefaultDateFormat) : this.emptyRaw;
		},
	});
}

export interface DateFieldOptions extends FieldOptions<string | undefined, Date | undefined, any, any>, DateConverterOptions {
}

export class DateField extends Field<string | undefined, Date | undefined> {
	constructor(
		options?: DateFieldOptions
	) {
		super(
			converters.maybe(dateConverter(options)),
			Object.assign({}, options, { postprocess: true }) // trigger formatting after blur
		);
	}
}
