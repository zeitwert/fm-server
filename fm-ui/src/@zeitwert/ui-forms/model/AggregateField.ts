
import { Aggregate, asEnumerated, Enumerated } from "@zeitwert/ui-model";
import { AccessorDependentQuery, Controlled, Converter, Field, FieldOptions } from "mstform";
import { AggregateSource, aggregateSource } from "./AggregateSource";

export interface AggregateConverterOptions<T extends Aggregate> {
}

// default Controlled, but only works with accessor, not with explicit values & onChange
// so it is be overridden in Select, Combobox, etc.
const enumeratedAggregate: Controlled = (accessor) => {
	return {
		value: accessor.raw?.id,
		onChange: (e: React.ChangeEvent<HTMLSelectElement>) => {
			const enumerated = e.target.value ? accessor.references.getById(e.target.value) : undefined;
			accessor.setRaw(enumerated);
		},
	};
};

export function aggregateConverter<T extends Aggregate>(options?: AggregateConverterOptions<T>): Converter<Enumerated | undefined, T | undefined> {
	return new Converter<Enumerated | undefined, T | undefined>({
		emptyRaw: undefined,
		emptyValue: undefined,
		convert(raw, { accessor }) {
			if (raw === this.emptyRaw) {
				return this.emptyValue;
			}
			return raw?.id as T | undefined;
		},
		render(value) {
			if (!value) {
				return this.emptyRaw;
			}
			return asEnumerated(value);
		},
		defaultControlled: enumeratedAggregate
	});
}

export interface AggregateFieldOptions<T extends Aggregate> extends FieldOptions<Enumerated | undefined, T | undefined, any, any>, AggregateConverterOptions<T> {
	source: AggregateSource;
	dependentQuery?: AccessorDependentQuery<any>; // Form will do [field].references.autoLoadReaction() in form constructor
}

export class AggregateField<T extends Aggregate> extends Field<Enumerated | undefined, T | undefined> {
	constructor(options: AggregateFieldOptions<T>) {
		const aggregateOptions = Object.assign(
			{},
			options ?? {},
			{
				references: {
					source: aggregateSource(options.source),
					dependentQuery: options.dependentQuery
				}
			}
		);
		super(aggregateConverter, aggregateOptions);
	}
}
