
import { converters, Field } from "mstform";

export class BooleanField extends Field<boolean | undefined, boolean | undefined> {
	constructor() {
		super(converters.boolean());
	}
}
