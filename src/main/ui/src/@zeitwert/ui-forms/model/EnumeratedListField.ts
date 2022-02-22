
import { Enumerated } from "@zeitwert/ui-model";
import { AccessorDependentQuery, converters, Field, FieldOptions, StringConverter } from "mstform";
import { enumeratedConverter } from "./EnumeratedField";
import { enumeratedSource, EnumSource } from "./EnumeratedHelpers";

export interface EnumeratedListConverterOptions {
}

export function enumeratedListConverter(options?: EnumeratedListConverterOptions) {
	return new StringConverter<Enumerated[] | undefined>({
		emptyRaw: "",
		emptyValue: [],
		convert(raw, { accessor }) {
			if (raw === this.emptyRaw) {
				return this.emptyValue;
			}
			return [];
		},
		render(value) {
			return value ? value.map(v => v.name).join(" ") : this.emptyRaw;
		},
	});
}

export interface EnumeratedListFieldOptions extends FieldOptions<string | undefined, Enumerated | undefined, any, any>, EnumeratedListConverterOptions {
	source: EnumSource;
	dependentQuery?: AccessorDependentQuery<any>; // don't forget to do [field].references.autoLoadReaction() in form constructor
}

export class EnumeratedListField extends Field<string | undefined, Enumerated | undefined> {
	constructor(options?: EnumeratedListFieldOptions) {
		super(
			converters.maybe(enumeratedConverter),
			Object.assign({}, options || {}, enumeratedSource(options?.source!, options?.dependentQuery))
		);
	}
}
