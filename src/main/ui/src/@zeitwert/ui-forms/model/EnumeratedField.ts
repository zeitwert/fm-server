
import { Enumerated } from "@zeitwert/ui-model";
import { toJS } from "mobx";
import { AccessorDependentQuery, Controlled, Converter, Field, FieldOptions } from "mstform";
import { enumeratedSource, EnumSource } from "./EnumeratedSource";


type MaybeEnumerated = Enumerated | undefined;
export interface EnumeratedConverterOptions {
}

// default Controlled, but only works with accessor, not with explicit values & onChange
// so it is be overridden in Select, Combobox, etc.
const enumerated: Controlled = (accessor) => {
	return {
		value: accessor.raw?.id,
		onChange: (e: React.ChangeEvent<HTMLSelectElement>) => {
			const enumerated = e.target.value ? accessor.references.getById(e.target.value) : undefined;
			accessor.setRaw(enumerated);
		},
	};
};

export function enumeratedConverter(options?: EnumeratedConverterOptions): Converter<MaybeEnumerated, MaybeEnumerated> {
	return new Converter<MaybeEnumerated, MaybeEnumerated>({
		emptyRaw: undefined,
		emptyValue: undefined,
		render(value) {
			return toJS(value);
		},
		convert(raw, { accessor }) {
			return raw;
		},
		defaultControlled: enumerated
	});
}

export interface EnumeratedFieldOptions extends FieldOptions<MaybeEnumerated, MaybeEnumerated, any, any>, EnumeratedConverterOptions {
	source: EnumSource;
	dependentQuery?: AccessorDependentQuery<any>; // Form will do [field].references.autoLoadReaction() in form constructor
}

export class EnumeratedField extends Field<MaybeEnumerated, MaybeEnumerated> {
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
		super(enumeratedConverter, enumOptions);
	}
}
