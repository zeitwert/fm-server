
import { Enumerated } from "@zeitwert/ui-model";
import { AccessorDependentQuery, converters, Field, FieldOptions, StringConverter } from "mstform";
import { enumeratedSource, EnumSource } from "./EnumeratedSource";

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

export interface EnumeratedListFieldOptions extends FieldOptions<string | undefined, Enumerated[] | undefined, any, any>, EnumeratedListConverterOptions {
	source: EnumSource;
	dependentQuery?: AccessorDependentQuery<any>; // Form will do [field].references.autoLoadReaction() in form constructor
}

export class EnumeratedListField extends Field<string | undefined, Enumerated[] | undefined> {
	constructor(options: EnumeratedListFieldOptions) {
		const enumListOptions = Object.assign(
			{},
			options ?? {},
			{
				references: {
					source: enumeratedSource(options.source),
					dependentQuery: options.dependentQuery
				}
			}
		);
		super(converters.maybe(enumeratedListConverter), enumListOptions);
	}
}
