
import { converters, Field, FieldOptions } from "mstform";

export class NumberField extends Field<string | undefined, number | undefined> {
	constructor(
		options?: FieldOptions<string | undefined, number | undefined, any, any>
	) {
		super(
			converters.maybe(converters.number),
			Object.assign({}, options, { postprocess: true }) // trigger formatting after blur
		);
	}
}
