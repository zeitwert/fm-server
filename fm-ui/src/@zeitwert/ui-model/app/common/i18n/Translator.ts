import { Language } from "./Language";
import { Locale } from "./Locale";

export interface Translator {
	currentLocale(): Locale;

	currentLanguage(): Language;

	translate(key: string, locale?: Language | Locale): string;
}
