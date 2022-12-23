
import { converters, Field } from "mstform";

export class IdField extends Field<string, string> {
	constructor() {
		super(converters.string);
	}
}
