
import { Enumerated } from "@zeitwert/ui-model";
import { AccessorDependentQuery, converters, Field, FieldOptions, StringConverter } from "mstform";
import { enumeratedSource, EnumSource } from "./EnumeratedSource";

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
			return accessor.references.getById(raw);
		},
		render(value) { // toString() to support numeric ids (f.ex. User)
			return value ? value.id.toString() : "";
		},
	});
}

export interface EnumeratedFieldOptions extends FieldOptions<string | undefined, Enumerated | undefined, any, any>, EnumeratedConverterOptions {
	source: EnumSource;
	dependentQuery?: AccessorDependentQuery<any>; // Form will do [field].references.autoLoadReaction() in form constructor
}

export class EnumeratedField extends Field<string | undefined, Enumerated | undefined> {
	constructor(options: EnumeratedFieldOptions) {
		const enumOptions = Object.assign(
			{},
			options ?? {},
			{
				references: {
					source: enumeratedSource(options.source),
					dependentQuery: options.dependentQuery
				}
			}
		);
		super(converters.maybe(enumeratedConverter), enumOptions);
	}
}
