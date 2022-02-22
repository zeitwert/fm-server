
import { Enumerated } from "@comunas/ui-model";
import { getSnapshot } from "mobx-state-tree";
import { AccessorDependentQuery, converters, Field, FieldOptions, StringConverter } from "mstform";
import { enumeratedSource, EnumSource } from "./EnumeratedHelpers";

export interface EnumeratedConverterOptions {
}

export function enumeratedConverter(options?: EnumeratedConverterOptions) {
	return new StringConverter<Enumerated | undefined>({
		emptyRaw: "",
		emptyValue: undefined,
		convert(raw, { accessor }) {
			if (raw === this.emptyRaw) {
				return this.emptyValue;
			}
			return getSnapshot(accessor.references.getById(raw));
		},
		render(value) {
			return value ? value.id : "";
		},
	});
}

export interface EnumeratedFieldOptions extends FieldOptions<string | undefined, Enumerated | undefined, any, any>, EnumeratedConverterOptions {
	source: EnumSource;
	dependentQuery?: AccessorDependentQuery<any>; // don't forget to do [field].references.autoLoadReaction() in form constructor
}

export class EnumeratedField extends Field<string | undefined, Enumerated | undefined> {
	constructor(options: EnumeratedFieldOptions) {
		super(
			converters.maybe(enumeratedConverter),
			Object.assign({}, options || {}, enumeratedSource(options.source, options.dependentQuery))
		);
	}
}
