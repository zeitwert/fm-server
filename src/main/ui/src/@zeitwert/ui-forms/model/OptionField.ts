
import { Enumerated } from "@zeitwert/ui-model";
import { ConverterOrFactory, Field, FieldOptions } from "mstform";

export interface OptionFieldOptions<R, V> extends FieldOptions<R, V, any, any> {
	options: Enumerated[];
}

export class OptionField<R, V> extends Field<R, V> {
	constructor(converter: ConverterOrFactory<R, V>, options: OptionFieldOptions<R, V>) {
		super(converter, Object.assign({}, options));
	}
}
