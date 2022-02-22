
import { ConversionError, Field, FieldOptions, StringConverter } from "mstform";

type StringOptions = {
	maxLength?: number;
};

function string(options?: StringOptions) {
	return new StringConverter<string>({
		emptyRaw: "",
		emptyValue: "",
		convert(raw) {
			if (options?.maxLength && options.maxLength < raw.length) {
				throw new ConversionError("exceedsMaxLength");
			}
			return raw;
		},
		render(value) {
			return value || this.emptyRaw;
		},
		preprocessRaw(raw: string): string {
			return raw.trim();
		},
	});
}

export class TextField extends Field<string, string | undefined> {
	constructor(
		options?: FieldOptions<string, string | undefined, any, any>
	) {
		super(string(), options);
	}
}
