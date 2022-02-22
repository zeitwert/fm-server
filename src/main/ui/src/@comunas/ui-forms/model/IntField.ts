
import { ConversionError, converters, Field, FieldOptions, StringConverter } from "mstform";

export interface IntConverterOptions {
	minLength?: number;
	maxLength?: number;
	minValue?: number;
	maxValue?: number;
}

export function intConverter(options?: IntConverterOptions) {
	return new StringConverter<number | undefined>({
		emptyRaw: "",
		emptyValue: undefined,
		convert(raw) {
			if (!raw) {
				return this.emptyValue;
			}
			if (!/^(-|\+)?([0-9]+)$/.test(raw)) {
				throw new ConversionError("default");
			}
			if (options?.maxLength != null && raw.length > options?.maxLength) {
				throw new ConversionError("exceedsMaxLength");
			} else if (options?.minLength != null && raw.length < options?.minLength) {
				throw new ConversionError("subceedsMinLength");
			}
			const value = Number(raw);
			if (options?.maxValue != null && value > options?.maxValue) {
				throw new ConversionError("exceedsMaxValue");
			} else if (options?.minValue != null && value < options?.minValue) {
				throw new ConversionError("subceedsMinValue");
			}
			return value;
		},
		render(value) {
			return value ? value.toString() : this.emptyRaw;
		},
	});
}

export interface IntFieldOptions extends FieldOptions<string | undefined, number | undefined, any, any>, IntConverterOptions {
}

export class IntField extends Field<string | undefined, number | undefined> {
	constructor(
		options?: IntFieldOptions
	) {
		super(converters.maybe(intConverter(options)), options);
	}
}
