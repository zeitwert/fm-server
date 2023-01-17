
import { converters, Field, FieldOptions } from "mstform";

export interface DateConverterOptions {
}

export interface DateFieldOptions extends FieldOptions<string | undefined, Date | undefined, any, any>, DateConverterOptions {
}

export class DateField extends Field<string | undefined, Date | undefined> {
	constructor(
		options?: DateFieldOptions
	) {
		super(
			converters.maybe(converters.object),
			options
		);
	}
}
