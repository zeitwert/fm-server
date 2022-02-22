import { Currency } from "./Currency";
import { Language } from "./Language";
import { Locale } from "./Locale";

export interface Formatter {
	currentLocale(): Locale | Language;

	currentLanguage(): Language;

	valueFormatter(locale: Locale | Language, digits: number): Intl.NumberFormat;

	formatValue(value: number, digits?: number, locale?: Locale | Language): string;

	formatPercent(value: number, digits?: number, locale?: Locale | Language): string;

	percentFormatter(locale: Locale | Language, digits: number): Intl.NumberFormat;

	amountFormatter(locale: Locale | Language, currency: Currency, digits: number): Intl.NumberFormat;

	formatAmount(amount: number, currency: Currency, digits?: number, locale?: Locale | Language): string;
}
